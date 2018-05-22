package de.ovgu.spldev.pclocator;


import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class MockSystemHeadersPresenceConditionLocator extends SimplePresenceConditionLocator {
    boolean isFeatureCoPPImplementation = false;
    ArrayList<String> mockFiles = new ArrayList<>();
    HashSet<Path> seenFilePaths = new HashSet<>();

    // Typedefs of the standard library. SuperC and TypeChef won't parse when types are unresolved,
    // so these are just typedef'd to int. Obtained from the POSIX specification: http://pubs.opengroup.org/onlinepubs/007904975/
    String standardTypes = "bool size_t ptrdiff_t sig_atomic_t wchar_t wint_t imaxdiv_t int_fast32_t fenv_t fexcept_t " +
            "int8_t int16_t int32_t int64_t uint8_t uint16_t uint32_t uint64_t int_least8_t int_least16_t " +
            "int_least32_t int_least64_t uint_least8_t uint_least16_t uint_least32_t uint_least64_t " +
            "int_fast8_t int_fast16_t int_fast32_t int_fast64_t uint_fast8_t uint_fast16_t uint_fast32_t uint_fast64_t " +
            "intptr_t uintptr_t intmax_t uintmax_t complex imaginary wctrans_t wctype_t mbstate_t va_list " +
            "clock_t time_t clockid_t timer_t div_t ldiv_t lldiv_t FILE fpos_t sigset_t pid_t float_t double_t " +
            "jmp_buf sigjmp_buf";

    // More typedefs, specifically added for analyzing Busybox.
    String customTypes = "uid_t mode_t gid_t off_t DIR socklen_t ssize_t sa_family_t nfds_t speed_t context_t " +
            "security_context_t";

    String[] types = (standardTypes + " " + customTypes).split(" ");

    MockSystemHeadersPresenceConditionLocator(Implementation implementation, Options options) {
        super(implementation, options);
        if (implementation instanceof FeatureCoPPPresenceConditionLocatorImplementation)
            isFeatureCoPPImplementation = true;
    }

    // We assume that system header files (#include <...>) do not include variability
    // and therefore may be safely ignored.
    protected String modifyFilePath(String filePathString) {
        if (isFeatureCoPPImplementation)
            return filePathString;
        Path filePath = Paths.get(filePathString);
        Options options = _implementation.getOptions();
        ArrayList<String> includeDirectories = options != null
                ? new ArrayList<>(Arrays.asList(options.getIncludeDirectories()))
                : new ArrayList<>();
        includeDirectories.remove(Arguments.getMockDirectory());

        processFile(filePath, includeDirectories);

        return filePathString;
    }

    private void processFile(Path filePath, ArrayList<String> includeDirectories) {
        seenFilePaths.add(filePath.toAbsolutePath().normalize());
        ArrayList<String> currentIncludeDirectories = (ArrayList<String>) includeDirectories.clone();
        currentIncludeDirectories.add(filePath.getParent().toAbsolutePath().normalize().toString());

        try (Stream<String> lineContentsStream = Files.lines(filePath)) {
            lineContentsStream.forEach(line -> {
                String systemIncludeFile = PreprocessorHelpers.getSystemIncludeFile(line),
                        userIncludeFile = PreprocessorHelpers.getUserIncludeFile(line);
                if (systemIncludeFile != null) {
                    Path newFilePath = Paths.get(Arguments.getMockDirectory(), systemIncludeFile);
                    try {
                        Files.createDirectories(newFilePath.getParent());
                        FileWriter fileWriter = new FileWriter(newFilePath.toFile());
                        for (String type : types)
                            fileWriter.write("typedef int " + type + ";\n");
                        fileWriter.close();
                        mockFiles.add(systemIncludeFile);
                    } catch (FileAlreadyExistsException ignored) {
                        mockFiles.add(systemIncludeFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (userIncludeFile != null)
                    for (String directoryString : currentIncludeDirectories) {
                        Path userIncludeFilePath = Paths.get(directoryString).resolve(userIncludeFile);
                        if (userIncludeFilePath.toFile().exists() && !seenFilePaths.contains(userIncludeFilePath))
                            processFile(userIncludeFilePath, includeDirectories);
                    }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected HashMap<Integer, PresenceCondition> modifyPresenceConditions
            (HashMap<Integer, PresenceCondition> locatedPresenceConditions, String[] lineContents) {
        locatedPresenceConditions = super.modifyPresenceConditions(locatedPresenceConditions, lineContents);
        if (isFeatureCoPPImplementation)
            return locatedPresenceConditions;

        for (Map.Entry<Integer, PresenceCondition> entry : locatedPresenceConditions.entrySet()) {
            if (mockFiles.isEmpty())
                entry.getValue().history().add("Mocked out no header files.");
            else
                entry.getValue().history().add("Mocked out the following header files: %s. " +
                        "This is done because the parser needs these files to be present, " +
                        "but their content is not relevant for variability.", String.join(", ", mockFiles));
        }

        return locatedPresenceConditions;
    }
}

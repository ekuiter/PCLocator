package de.ovgu.spldev.pclocator;


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
                        Files.createFile(newFilePath);
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
            entry.getValue().history().add("Mocked out the following header files: %s. " +
                    "This is done because the parser needs these files to be present, " +
                    "but their content is not relevant for variability.", String.join(", ", mockFiles));
        }

        return locatedPresenceConditions;
    }
}

package de.ovgu.spldev.pclocator;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MockSystemHeadersPresenceConditionLocator extends SimplePresenceConditionLocator {
    boolean isFeatureCoPPImplementation = false;
    ArrayList<String> mockFiles = new ArrayList<>();

    MockSystemHeadersPresenceConditionLocator(Implementation implementation, Options options) {
        super(implementation, options);
        if (implementation instanceof FeatureCoPPPresenceConditionLocatorImplementation)
            isFeatureCoPPImplementation = true;
    }

    // We assume that system header files (#include <...>) do not include variability
    // and therefore may be safely ignored.
    protected String modifyFilePath(String filePath) {
        if (isFeatureCoPPImplementation)
            return filePath;

        try (Stream<String> lineContentsStream = Files.lines(Paths.get(filePath))) {
            lineContentsStream.forEach(line -> {
                String mockFile = PreprocessorHelpers.getSystemIncludeFile(line);
                if (mockFile != null) {
                    Path newFilePath = Paths.get(Arguments.getMockDirectory(), mockFile);
                    try {
                        Files.createDirectories(newFilePath.getParent());
                        Files.createFile(newFilePath);
                        mockFiles.add(mockFile);
                    } catch (FileAlreadyExistsException ignored) {
                        mockFiles.add(mockFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filePath;
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

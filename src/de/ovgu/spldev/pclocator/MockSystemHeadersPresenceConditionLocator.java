package de.ovgu.spldev.pclocator;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class MockSystemHeadersPresenceConditionLocator extends SimplePresenceConditionLocator {
    MockSystemHeadersPresenceConditionLocator(Implementation implementation, Options options) {
        super(implementation, options);
    }

    // We assume that system header files (#include <...>) do not include variability
    // and therefore may be safely ignored.
    protected String modifyFilePath(String filePath) {
        try (Stream<String> lineContentsStream = Files.lines(Paths.get(filePath))) {
            lineContentsStream.forEach(line -> {
                String file = PreprocessorHelpers.getSystemIncludeFile(line);
                if (file != null) {
                    Path newFilePath = Paths.get(Arguments.getMockDirectory(), file);
                    try {
                        Files.createDirectories(newFilePath.getParent());
                        Files.createFile(newFilePath);
                    } catch (FileAlreadyExistsException ignored) {
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
}

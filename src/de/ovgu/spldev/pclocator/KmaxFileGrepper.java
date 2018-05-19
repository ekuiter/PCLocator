package de.ovgu.spldev.pclocator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gets presence conditions from a Kmax result file. Requires a shell with basic commands (cat, grep, cut),
 * so a *nix system for a simple implementation. We could also sort the Kmax file and do binary search to
 * locate presence conditions, but in practice the "grep" approach works fine even for the Linux kernel.
 */
public class KmaxFileGrepper {
    PresenceConditionLocator.Implementation implementation;
    String kmaxFilePath;
    String projectRootPath;
    String filePath;
    private PresenceCondition[] kmaxPresenceConditions = null;
    private static HashMap<String, Boolean> notified = new HashMap<>();

    public KmaxFileGrepper(PresenceConditionLocator.Implementation implementation, String kmaxFilePath, String projectRootPath, String filePath) {
        this.implementation = implementation;
        this.kmaxFilePath = kmaxFilePath;
        this.projectRootPath = projectRootPath;
        this.filePath = filePath;
    }

    private ArrayList<String> searchKmaxFile(Path kmaxFilePath, Path objectFile) {
        ArrayList<String> componentSearches = new ArrayList<>();
        componentSearches.add("unit_pc " + objectFile + " ");
        for (Path component = objectFile.getParent(); component != null; component = component.getParent())
            componentSearches.add("subdir_pc " + component + " ");
        ArrayList<String> results = new ArrayList<>();

        // Scan the Kmax result file linearly for all path components occuring in the object file to
        // be searched for. This may be sped up by sorting the file first and then doing binary search,
        // but was tested and works fine for Kmax result files < 2 MiB.
        try {
            Files.lines(kmaxFilePath)
                    .filter(line -> {
                        for (String componentSearch : componentSearches)
                            if (line.startsWith(componentSearch)) {
                                componentSearches.remove(componentSearch); // found component, don't keep searching
                                return true;
                            }
                        return false;
                    })
                    .map(line -> {
                        String[] parts = line.split(" ");
                        return String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
                    })
                    .forEach(results::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    private PresenceCondition[] locatePresenceConditions(PresenceConditionLocator.Implementation implementation,
                                                             Path kmaxFilePath, Path projectRootPath, Path filePath) {
        if (!filePath.toString().endsWith(".c"))
            throw new RuntimeException("file " + filePath + " is not a C file");
        if (!Files.exists(filePath))
            throw new RuntimeException("file " + filePath + " does not exist");
        if (!filePath.startsWith(projectRootPath))
            throw new RuntimeException("project root " + projectRootPath + " does not contain " + filePath);

        Path objectFile = Paths.get(projectRootPath.relativize(filePath).toString().replaceAll("\\.c$", ".o"));

        // Both unit_pc and subdir_pc are unique if present, but there might be a presence condition
        // for every single path component - find them and transform the DNF formulas into presence conditions.
        return searchKmaxFile(kmaxFilePath, objectFile).stream().map(implementation::fromDNF).toArray(PresenceCondition[]::new);
    }

    public void locatePresenceConditions() {
        if (kmaxPresenceConditions == null) {
            kmaxPresenceConditions = locatePresenceConditions(implementation, Paths.get(kmaxFilePath), Paths.get(projectRootPath), Paths.get(filePath));
            if (!notified.getOrDefault(kmaxFilePath, false))
                Log.notice("Kmax presence condition: %s",
                        Stream.of(kmaxPresenceConditions).map(PresenceCondition::toString).collect(Collectors.joining("&&")));
            notified.put(kmaxFilePath, true);
        }
    }

    public PresenceCondition modifyPresenceCondition(PresenceCondition presenceCondition) {
        locatePresenceConditions();
        for (int i = kmaxPresenceConditions.length - 1; i >= 0; i--)
            presenceCondition = kmaxPresenceConditions[i].and(presenceCondition);
        return presenceCondition;
    }
}

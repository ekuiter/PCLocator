package de.ovgu.spldev.pclocator;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gets presence conditions from a Kmax result file. Requires a shell with basic commands (cat, grep, cut),
 * so a *nix system for a simple implementation. We could also sort the Kmax file and do binary search to
 * locate presence conditions, but in practice the "grep" approach works fine even for the Linux kernel.
 */
public class KmaxFileGrepper {
    private static Escaper escaper;
    private PresenceCondition kmaxPresenceCondition;

    static {
        Escapers.Builder builder = Escapers.builder();
        builder.addEscape('\'', "'\"'\"'");
        escaper = builder.build();
    }

    public KmaxFileGrepper(String kmaxFilePath, String projectRootPath, String filePath) {
        kmaxPresenceCondition = locatePresenceCondition(Paths.get(kmaxFilePath), Paths.get(projectRootPath), Paths.get(filePath));
    }

    private String run(String... args) {
        StringBuilder output = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder(args);

        try {
            Process p = pb.start();
            p.waitFor();
            if (p.exitValue() != 0)
                throw new RuntimeException("exit value was " + p.exitValue());
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null)
                output.append(line).append("\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return output.toString().trim();
    }

    private String runInShell(String command, String... args) {
        for (int i = 0; i < args.length; i++)
            args[i] = escaper.escape(args[i]);
        return run("/bin/sh", "-c", String.format(command, (Object[]) args));
    }

    private PresenceCondition locatePresenceCondition(Path kmaxFilePath, Path projectRootPath, Path filePath) {
        if (!filePath.toString().endsWith(".c"))
            throw new RuntimeException("file " + filePath + " is not a C file");
        if (!Files.exists(filePath))
            throw new RuntimeException("file " + filePath + " does not exist");
        if (!filePath.startsWith(projectRootPath))
            throw new RuntimeException("project root " + projectRootPath + " does not contain " + filePath);

        String search = "^unit_pc " + projectRootPath.relativize(filePath).toString().replaceAll("\\.c$", ".o") + " ";
        String result = runInShell("cat '%s' | grep '%s' | cut -d ' ' -f 3-", kmaxFilePath.toAbsolutePath().toString(), search);

        if (result.length() == 0)
            return PresenceCondition.NOT_FOUND;
        return TypeChefPresenceCondition.fromDNF(result);
    }

    public PresenceCondition modifyPresenceCondition(PresenceCondition presenceCondition) {
        return kmaxPresenceCondition.and(presenceCondition);
    }
}

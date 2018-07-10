package de.ovgu.spldev.pclocator;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Arguments {
    String[] args;
    String[] unusedArgs;
    static boolean warnedPlatformHeader = false;

    Arguments(String[] args) {
        this.args = args;
        this.unusedArgs = args.clone();
    }

    private boolean allowed(String arg, String[] allowedArgs) {
        for (String currentArg : allowedArgs)
            if (arg.equals(currentArg))
                return true;
        return false;
    }

    private boolean has(String arg, boolean includeLastArg) {
        boolean found = false;
        for (int i = 0; i < args.length - (includeLastArg ? 0 : 1); i++) {
            String currentArg = args[i];
            if (arg.equals(currentArg) && !found) {
                unusedArgs[i] = null;
                found = true;
            } else if (arg.equals(currentArg))
                throw new RuntimeException(arg + " may only be supplied once");
        }
        return found;
    }

    private boolean has(String arg) {
        return has(arg, false);
    }

    private String[] getMany(String arg) {
        ArrayList<String> results = new ArrayList<>();

        for (int i = 0; i < args.length - 1; i++)
            if (arg.equals(args[i])) {
                if (i < args.length - 2) {
                    unusedArgs[i] = unusedArgs[i + 1] = null;
                    results.add(args[i + 1]);
                } else
                    throw new RuntimeException("no value supplied for " + arg);
            }

        return results.toArray(new String[0]);
    }

    private String get(String arg) {
        if (!has(arg))
            return null;
        return getMany(arg)[0];
    }

    private boolean ensure(boolean b, String msg) {
        if (!b)
            throw new RuntimeException(msg);
        return true;
    }

    void ensureValidUsage() {
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            String arg = unusedArgs[i];
            if (arg != null)
                msg.append(" ").append(arg);
        }
        if (msg.length() > 0)
            throw new RuntimeException("the following arguments were not recognized:" + msg);
    }

    String getUsage() {
        return "usage: java -jar PCLocator.jar [options...] location\n\n" +
                "options:\n" +
                "  --help         displays usage\n\n" +
                "mode of operation:\n" +
                "  --parser       choose a parser (for <file>:<line> locations):\n" +
                "      typechef     use TypeChef parser\n" +
                "      superc       use SuperC parser\n" +
                "      xtc          same as superc\n" +
                "      featurecopp  use FeatureCoPP parser\n" +
                "      merge        combines all the parsers above, prioritizes retrieval of a\n" +
                "                   configuration over the correct presence condition (default)\n" +
                "  --annotator    override the default annotator (for <file> locations):\n" +
                "      typechef     annotate file with TypeChef parser\n" +
                "      superc       annotate file with SuperC parser\n" +
                "      xtc          same as superc\n" +
                "      featurecopp  annotate file with FeatureCoPP parser\n" +
                "      merge        annotate file with Merge parser\n" +
                "      equivalent   whether TypeChef and SuperC yield the same results\n" +
                "      all          annotate file with TypeChef, SuperC and\n" +
                "                   FeatureCoPP (default)\n" +
                "  --locator      choose a location algorithm, possible values include:\n" +
                "      simple               returns raw presence conditions found by the parser\n" +
                "      mockSystemHeaders    like simple, but replaces system headers such as\n" +
                "                           stdio.h with minimal mocks containing typedefs\n" +
                "      ignorePreprocessor   like ignoreSystemHeaders, but does not return\n" +
                "                           presence conditions for preprocessor lines\n" +
                "      deduceNotFound       like ignorePreprocessor, but deduces missing\n" +
                "                           presence conditions from surrounding lines (default)\n" +
                "      kmax                 like deduceNotFound, but considers build system\n" +
                "                           constraints, requires --kmaxfile and --projectroot\n" +
                "  --explain      prints an explanation for how the presence condition\n" +
                "                 or configuration space was located\n" +
                "  --legacy       use deprecated parser implementations, not recommended\n\n" +
                "parser options:\n" +
                "  -I             pass additional include directory to the parser\n" +
                "  --platform     pass additional header file to the parser,\n" +
                "                 generate this using: echo - | gcc -dM - -E -std=gnu99\n\n" +
                "Kmax options:\n" +
                "  --kmaxfile     pass a Kmax presence condition file (with unit_pc's and\n" +
                "                 subdir_pc's) to consider build system constraints\n" +
                "  --projectroot  when specifying a --kmaxfile, the project root directory\n" +
                "                 relative to the unit_pc's/subdir_pc's must be specified\n\n" +
                "configuration options:\n" +
                "  --configure    pass a feature model in DIMACS format for deriving\n" +
                "                 concrete configurations instead of presence conditions\n" +
                "  --limit        maximum number of configurations to be derived\n" +
                "                 (default: no limit)\n" +
                "  --timelimit    time limit for deriving a configuration space\n" +
                "                 of form \"WWd XXh YYm ZZs\" (default: no time limit)\n" +
                "  --format       choose a configuration output format, possible values include:\n" +
                "      human      output enabled features in a human-readable format (default)\n" +
                "      flags      output configuration as -D flags which can be passed to GCC\n" +
                "      config     output a .config file usable with the Kbuild build system\n\n" +
                "The location can have the form\n" +
                "  <file>:<line>  in which case only the given line will be analyzed, or\n" +
                "  <file>         in which case a tabular analysis of all lines in the file\n" +
                "                 will be printed.";
    }

    boolean isHelp() {
        return has("--help", true);
    }

    boolean isAnnotating() {
        return getLocation() != null && !Location.isValidLocation(getLocation());
    }

    boolean isExplain() {
        boolean isExplain = has("--explain");
        if (isAnnotating() && isExplain)
            throw new RuntimeException("only individual lines can be explained");
        return isExplain;
    }

    boolean isLegacy() {
        return has("--legacy");
    }

    String getFormatKind() {
        String format = get("--format");
        if (format == null)
            return "human";

        if (getDimacsFilePath() == null)
            throw new RuntimeException("--configure has to be specified when using --format");

        String[] allowedArgs = {"human", "flags", "config"};
        if (allowed(format, allowedArgs))
            return format;
        else
            throw new RuntimeException("invalid format " + format);
    }

    String getParserKind() {
        String parser = get("--parser");
        if (parser == null)
            return "merge";

        if (isAnnotating())
            throw new RuntimeException("use --annotator to specify parsers when annotating a whole file");

        String[] allowedArgs = {"typechef", "superc", "xtc", "featurecopp", "merge"};
        if (allowed(parser, allowedArgs))
            return parser;
        else
            throw new RuntimeException("invalid parser " + parser);
    }

    String[] getAnnotatorKinds() {
        String[] annotators = getMany("--annotator");
        if (annotators.length == 0)
            return new String[]{"all"};

        if (!isAnnotating())
            throw new RuntimeException("only a whole file can be annotated, not individual lines");

        String[] allowedArgs = {"typechef", "superc", "xtc", "featurecopp", "merge", "equivalent", "all"};
        for (String annotator : annotators) {
            if (!allowed(annotator, allowedArgs))
                throw new RuntimeException("invalid annotator " + annotator);
        }

        return annotators;
    }

    String getLocatorKind() {
        String locator = get("--locator");
        if (locator == null)
            return "deduceNotFound";

        String[] allowedArgs = {"simple", "mockSystemHeaders", "ignorePreprocessor", "deduceNotFound", "kmax"};
        if (allowed(locator, allowedArgs)) {
            if (locator.equals("kmax")) {
                if (get("--kmaxfile") == null)
                    throw new RuntimeException("--kmaxfile has to be specified when using --locator kmax");
                if (get("--projectroot") == null)
                    throw new RuntimeException("--projectroot has to be specified when using --locator kmax");
            }
            return locator;
        } else
            throw new RuntimeException("invalid locator " + locator);
    }

    String getDimacsFilePath() {
        String dimacsFilePath = get("--configure");
        if (dimacsFilePath != null)
            return Location.validateFilePath(dimacsFilePath);
        return null;
    }

    Integer getLimit() {
        String limit = get("--limit");
        if (limit != null && getDimacsFilePath() == null)
            throw new RuntimeException("--configure has to be specified when using --limit");
        return limit != null ? Integer.parseInt(limit) : null;
    }

    String getTimeLimit() {
        String timeLimit = get("--timelimit");
        if (timeLimit != null && getDimacsFilePath() == null)
            throw new RuntimeException("--configure has to be specified when using --timelimit");
        return timeLimit;
    }

    String getKmaxFilePath() {
        String kmaxFilePath = get("--kmaxfile");
        if (kmaxFilePath != null) {
            if (get("--projectroot") == null)
                throw new RuntimeException("--projectroot has to be specified when using --kmaxfile");
            if (!getLocatorKind().equals("kmax"))
                throw new RuntimeException("--locator kmax has to be specified when using --kmaxfile");
            return Location.validateFilePath(kmaxFilePath);
        }
        return null;
    }

    String getProjectRootPath() {
        String projectRootPath = get("--projectroot");
        if (projectRootPath != null) {
            if (get("--kmaxfile") == null)
                throw new RuntimeException("--kmaxfile has to be specified when using --projectroot");
            if (!getLocatorKind().equals("kmax"))
                throw new RuntimeException("--locator kmax has to be specified when using --projectroot");
        }
        return projectRootPath;
    }

    public static String getJarDirectory() {
        String jarDirectory;
        try {
            File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            jarDirectory = jarFile.getParentFile().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return jarDirectory;
    }

    public static String getMockDirectory() {
        return Paths.get(getJarDirectory(), "mock").toAbsolutePath().toString();
    }

    public static String getFeatureCoPPDirectory() {
        return Paths.get(getJarDirectory(), "FeatureCoPP").toAbsolutePath().toString();
    }

    String[] getIncludeDirectories() {
        String[] includeDirectories = getMany("-I");
        for (int i = 0; i < includeDirectories.length; i++)
            includeDirectories[i] = Paths.get(includeDirectories[i]).toAbsolutePath().normalize().toString();
        if (!getLocatorKind().equals("simple")) {
            includeDirectories = Arrays.copyOf(includeDirectories, includeDirectories.length + 1);
            includeDirectories[includeDirectories.length - 1] = getMockDirectory();
        }
        return includeDirectories;
    }

    String getPlatformHeaderFilePath() {
        String platformHeaderFilePath = get("--platform");
        if (platformHeaderFilePath != null)
            return Location.validateFilePath(platformHeaderFilePath);
        return null;
    }

    public static void warnPlatformHeader() {
        if (!warnedPlatformHeader)
            Log.warning("no platform header supplied, see --help");
        warnedPlatformHeader = true;
    }

    String getLocation() {
        return args.length > 0 ? args[args.length - 1] : null;
    }
}

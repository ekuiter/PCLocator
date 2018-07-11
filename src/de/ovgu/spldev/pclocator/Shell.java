package de.ovgu.spldev.pclocator;

import xtc.lang.cpp.LegacySuperCPresenceConditionLocatorImplementation;
import xtc.lang.cpp.SuperCPresenceConditionLocatorImplementation;

import java.util.ArrayList;

public class Shell {
    PresenceConditionLocator typeChef, featureCoPP, superC, merge;
    AnnotatedFile.FileAnnotator equivalenceChecker;

    private PresenceConditionLocator.Options getPresenceConditionLocatorOptions(Arguments args) {
        return new PresenceConditionLocator.Options(args.getIncludeDirectories(), args.getPlatformHeaderFilePath());
    }

    private LegacyTypeChefPresenceConditionLocatorImplementation getTypeChefPresenceConditionLocatorImplementation(Arguments args) {
        return args.isLegacy()
                ? new LegacyTypeChefPresenceConditionLocatorImplementation()
                : new TypeChefPresenceConditionLocatorImplementation();
    }

    private LegacySuperCPresenceConditionLocatorImplementation getSuperCPresenceConditionLocatorImplementation(Arguments args) {
        return args.isLegacy()
                ? new LegacySuperCPresenceConditionLocatorImplementation()
                : new SuperCPresenceConditionLocatorImplementation();
    }

    private PresenceConditionLocator.Implementation getPresenceConditionLocatorImplementation(Arguments args) {
        String kind = args.getParserKind();
        if (kind.equals("typechef"))
            return getTypeChefPresenceConditionLocatorImplementation(args);
        else if (kind.equals("featurecopp"))
            return new FeatureCoPPPresenceConditionLocatorImplementation();
        else if (kind.equals("xtc") || kind.equals("superc"))
            return getSuperCPresenceConditionLocatorImplementation(args);
        else
            throw new RuntimeException("unknown parser kind " + kind);
    }

    private PresenceConditionLocator getPresenceConditionLocator
            (Arguments args, PresenceConditionLocator.Implementation implementation, PresenceConditionLocator.Options options) {
        String kind = args.getLocatorKind();
        if (kind.equals("simple"))
            return new SimplePresenceConditionLocator(implementation, options);
        else if (kind.equals("mockSystemHeaders"))
            return new MockSystemHeadersPresenceConditionLocator(implementation, options);
        else if (kind.equals("ignorePreprocessor"))
            return new IgnorePreprocessorPresenceConditionLocator(implementation, options);
        else if (kind.equals("kmax")) {
            String filePath = Location.getFilePathFromLocation(args.getLocation());
            KmaxFileGrepper kmaxFileGrepper = new KmaxFileGrepper(implementation, args.getKmaxFilePath(), args.getProjectRootPath(), filePath);
            return new KmaxPresenceConditionLocator(implementation, options, kmaxFileGrepper);
        } else if (kind.equals("deduceNotFound"))
            return new DeduceNotFoundPresenceConditionLocator(implementation, options);
        else
            throw new RuntimeException("unknown locator kind " + kind);
    }

    private AnnotatedFile.FileAnnotator extendPresenceConditionLocator(Arguments args, PresenceConditionLocator presenceConditionLocator) {
        String dimacsFilePath = args.getDimacsFilePath();
        return dimacsFilePath != null
                ? new ConfigurationSpaceLocator(presenceConditionLocator, dimacsFilePath, args.getLimit(), args.getTimeLimit())
                : presenceConditionLocator;
    }

    private AnnotatedFile.FileAnnotator[] getFileAnnotators(Arguments args, PresenceConditionLocator.Options options) {
        String[] kinds = args.getAnnotatorKinds();
        ArrayList<AnnotatedFile.FileAnnotator> fileAnnotators = new ArrayList<>();

        for (String kind : kinds) {
            if (kind.equals("typechef"))
                fileAnnotators.add(extendPresenceConditionLocator(args, typeChef));
            else if (kind.equals("featurecopp"))
                fileAnnotators.add(extendPresenceConditionLocator(args, featureCoPP));
            else if (kind.equals("equivalent"))
                fileAnnotators.add(equivalenceChecker);
            else if (kind.equals("all")) {
                fileAnnotators.add(extendPresenceConditionLocator(args, typeChef));
                fileAnnotators.add(extendPresenceConditionLocator(args, superC));
                fileAnnotators.add(extendPresenceConditionLocator(args, featureCoPP));
            } else if (kind.equals("xtc") || kind.equals("superc"))
                fileAnnotators.add(extendPresenceConditionLocator(args, superC));
            else if (kind.equals("merge"))
                fileAnnotators.add(extendPresenceConditionLocator(args, merge));
            else
                throw new RuntimeException("unknown annotator kind " + kind);
        }

        return fileAnnotators.toArray(new AnnotatedFile.FileAnnotator[0]);
    }

    private void analyze(PresenceConditionLocator presenceConditionLocator,
                         AnnotatedFile.FileAnnotator[] fileAnnotators, String location,
                         String dimacsFilePath, Integer limit, String timeLimit, boolean isExplain) {
        if (Location.isValidLocation(location)) {
            PresenceCondition presenceCondition = presenceConditionLocator.locatePresenceCondition(new Location(location));
            if (dimacsFilePath != null)
                presenceCondition.getSatisfyingConfigurationSpace(dimacsFilePath, limit, timeLimit).print(isExplain);
            else
                presenceCondition.print(isExplain);
        } else {
            AnnotatedFile annotatedFile = new AnnotatedFile(location);
            for (AnnotatedFile.FileAnnotator fileAnnotator : fileAnnotators)
                annotatedFile.addFileAnnotator(fileAnnotator);
            annotatedFile.print();
        }
    }

    public void run(String[] _args) {
        Arguments args = new Arguments(_args);
        String location = args.getLocation(),
                dimacsFilePath = args.getDimacsFilePath(),
                timeLimit = args.getTimeLimit();
        Integer limit = args.getLimit();
        boolean isExplain = args.isExplain(), isEvaluate = args.isEvaluate();
        Configuration.setFormatKind(args);

        if (args.isHelp() || location == null) {
            System.out.println(args.getUsage());
            return;
        }

        PresenceConditionLocator.Options options = getPresenceConditionLocatorOptions(args);
        typeChef = getPresenceConditionLocator(args, getTypeChefPresenceConditionLocatorImplementation(args), options);
        featureCoPP = getPresenceConditionLocator(args, new FeatureCoPPPresenceConditionLocatorImplementation(), options);
        superC = getPresenceConditionLocator(args, getSuperCPresenceConditionLocatorImplementation(args), options);
        merge = new MergePresenceConditionLocator(typeChef, superC, featureCoPP);
        equivalenceChecker = new PresenceConditionEquivalenceChecker("TypeChef == SuperC", typeChef, superC);

        PresenceConditionLocator presenceConditionLocator;
        presenceConditionLocator = args.getParserKind().equals("merge")
                ? merge
                : getPresenceConditionLocator(args, getPresenceConditionLocatorImplementation(args), options);
        AnnotatedFile.FileAnnotator[] fileAnnotators = getFileAnnotators(args, options);
        args.ensureValidUsage();

        try {
            if (isEvaluate)
                new Evaluator().run(presenceConditionLocator, new Location(location), dimacsFilePath);
            else
                analyze(presenceConditionLocator, fileAnnotators, location, dimacsFilePath, limit, timeLimit, isExplain);
        } catch (Exception e) {
            Log.error("%s", e);
            System.exit(1);
        }
    }
}

package de.ovgu.spldev.pclocator;

import xtc.lang.cpp.SuperCPresenceConditionLocatorImplementation;

import java.util.ArrayList;

public class PresenceConditionLocatorShell {
    private PresenceConditionLocator.Options getPresenceConditionLocatorOptions(Arguments args) {
        return new PresenceConditionLocator.Options(args.getIncludeDirectories());
    }

    private PresenceConditionLocator.Implementation getPresenceConditionLocatorImplementation(Arguments args) {
        String kind = args.getParserKind();
        if (kind.equals("typechef"))
            return new TypeChefPresenceConditionLocatorImplementation();
        else if (kind.equals("featurecopp"))
            return new FeatureCoPPPresenceConditionLocatorImplementation();
        else
            return new SuperCPresenceConditionLocatorImplementation();
    }

    private SimplePresenceConditionLocator getPresenceConditionLocator
            (Arguments args, PresenceConditionLocator.Implementation implementation, PresenceConditionLocator.Options options) {
        String kind = args.getLocatorKind();
        if (kind.equals("simple"))
            return new SimplePresenceConditionLocator(implementation, options);
        else if (kind.equals("mockSystemHeaders"))
            return new MockSystemHeadersPresenceConditionLocator(implementation, options);
        else if (kind.equals("ignorePreprocessor"))
            return new IgnorePreprocessorPresenceConditionLocator(implementation, options);
        else if (kind.equals(("deduceNotFound")))
            return new DeduceNotFoundPresenceConditionLocator(implementation, options);
        else
            return new PresenceConditionLocator(implementation, options);
    }

    private AnnotatedFile.FileAnnotator extendPresenceConditionLocator(Arguments args, SimplePresenceConditionLocator presenceConditionLocator) {
        String dimacsFilePath = args.getDimacsFilePath();
        if (dimacsFilePath != null)
            return new ConfigurationSpaceLocator(presenceConditionLocator, dimacsFilePath, args.getTimeLimit());
        else
            return presenceConditionLocator;
    }

    private AnnotatedFile.FileAnnotator[] getFileAnnotators(Arguments args, PresenceConditionLocator.Options options) {
        String[] kinds = args.getAnnotatorKinds();
        ArrayList<AnnotatedFile.FileAnnotator> fileAnnotators = new ArrayList<>();
        SimplePresenceConditionLocator
                typeChef = getPresenceConditionLocator(args, new TypeChefPresenceConditionLocatorImplementation(), options),
                featureCoPP = getPresenceConditionLocator(args, new FeatureCoPPPresenceConditionLocatorImplementation(), options),
                superC = getPresenceConditionLocator(args, new SuperCPresenceConditionLocatorImplementation(), options);
        AnnotatedFile.FileAnnotator equivalenceChecker = new PresenceConditionEquivalenceChecker("TypeChef == SuperC", typeChef, superC);

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
                fileAnnotators.add(equivalenceChecker);
            } else
                fileAnnotators.add(extendPresenceConditionLocator(args, superC));
        }

        return fileAnnotators.toArray(new AnnotatedFile.FileAnnotator[0]);
    }

    private void analyze(SimplePresenceConditionLocator presenceConditionLocator,
                         AnnotatedFile.FileAnnotator[] fileAnnotators, String location,
                         String dimacsFilePath, String timeLimit) {
        if (presenceConditionLocator.isValidLocation(location)) {
            PresenceCondition presenceCondition = presenceConditionLocator.locatePresenceCondition(location);
            if (dimacsFilePath != null)
                presenceCondition.getSatisfyingConfigurationSpace(dimacsFilePath, timeLimit).print();
            else
                presenceCondition.print();
        } else {
            AnnotatedFile annotatedFile = new AnnotatedFile(location);
            for (AnnotatedFile.FileAnnotator fileAnnotator : fileAnnotators)
                annotatedFile.addFileAnnotator(fileAnnotator);
            annotatedFile.print();
        }
    }

    public void run(String[] _args) {
        Arguments args = new Arguments(_args);
        PresenceConditionLocator.Implementation implementation = getPresenceConditionLocatorImplementation(args);
        PresenceConditionLocator.Options options = getPresenceConditionLocatorOptions(args);
        SimplePresenceConditionLocator presenceConditionLocator = getPresenceConditionLocator(args, implementation, options);
        AnnotatedFile.FileAnnotator[] fileAnnotators = getFileAnnotators(args, options);
        String location = args.getLocation(),
                dimacsFilePath = args.getDimacsFilePath(),
                timeLimit = args.getTimeLimit();
        args.ensureValidUsage();

        if (args.isHelp() || location == null) {
            System.out.println(args.getUsage());
            return;
        }

        try {
            analyze(presenceConditionLocator, fileAnnotators, location, dimacsFilePath, timeLimit);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}

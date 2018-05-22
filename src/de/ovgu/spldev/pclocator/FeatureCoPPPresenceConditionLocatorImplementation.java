package de.ovgu.spldev.pclocator;

import de.ovgu.spldev.featurecopp.config.Configuration;
import de.ovgu.spldev.featurecopp.filesystem.Filesystem;
import de.ovgu.spldev.featurecopp.lang.cpp.CPPAnalyzer;
import de.ovgu.spldev.featurecopp.log.Logger;
import de.ovgu.spldev.featurecopp.splmodel.FeatureModule;
import de.ovgu.spldev.featurecopp.splmodel.FeatureTable;
import de.ovgu.spldev.featurecopp.splmodel.FeatureTree;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FeatureCoPPPresenceConditionLocatorImplementation implements PresenceConditionLocator.Implementation {
    private Field featureTableField, featureOccurrencesField, ftreeField, enclosingField;

    FeatureCoPPPresenceConditionLocatorImplementation() {
        try {
            featureTableField = FeatureTable.class.getDeclaredField("featureTable");
            featureOccurrencesField = FeatureModule.class.getDeclaredField("featureOccurrences");
            ftreeField = FeatureModule.FeatureOccurrence.class.getDeclaredField("ftree");
            enclosingField = FeatureModule.FeatureOccurrence.class.getDeclaredField("enclosing");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        featureTableField.setAccessible(true);
        featureOccurrencesField.setAccessible(true);
        ftreeField.setAccessible(true);
        enclosingField.setAccessible(true);
    }

    public String getName() {
        return "FeatureCoPP";
    }

    public PresenceConditionLocator.Options getOptions() {
        return null;
    }

    public void setOptions(PresenceConditionLocator.Options options) {
    }

    public de.ovgu.spldev.pclocator.PresenceCondition getTrue() {
        return FeatureCoPPPresenceCondition.getTrue();
    }

    public PresenceCondition[] fromDNF(String formula) {
        return new PresenceCondition[]{FeatureCoPPPresenceCondition.fromDNF(formula)};
    }

    private HashMap<String, FeatureModule> getFeatureTable() {
        try {
            return (HashMap<String, FeatureModule>) featureTableField.get(FeatureTable.class);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<FeatureModule.FeatureOccurrence> getFeatureOccurences(FeatureModule featureModule) {
        try {
            return (ArrayList<FeatureModule.FeatureOccurrence>) featureOccurrencesField.get(featureModule);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private FeatureTree getFeatureTree(FeatureModule.FeatureOccurrence featureOccurrence) {
        try {
            return (FeatureTree) ftreeField.get(featureOccurrence);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private FeatureModule.FeatureOccurrence getEnclosingFeatureOccurence(FeatureModule.FeatureOccurrence featureOccurrence) {
        try {
            return (FeatureModule.FeatureOccurrence) enclosingField.get(featureOccurrence);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private FeatureTree getNestedFeatureTree(FeatureModule.FeatureOccurrence featureOccurrence) {
        FeatureModule.FeatureOccurrence enclosingFeatureOccurence = getEnclosingFeatureOccurence(featureOccurrence);
        if (enclosingFeatureOccurence != null) {
            FeatureTree featureTree = getFeatureTree(featureOccurrence),
                    previousFeatureTree = getNestedFeatureTree(enclosingFeatureOccurence),
                    nestedFeatureTree = new FeatureTree();
            nestedFeatureTree.setKeyword(featureTree.getKeyword());
            nestedFeatureTree.setRoot(new FeatureTree.LogAnd(previousFeatureTree.getRoot(), featureTree.getRoot(), "&&"));
            return nestedFeatureTree;
        } else
            return getFeatureTree(featureOccurrence);
    }

    private int getLevel(FeatureModule.FeatureOccurrence featureOccurrence) {
        FeatureModule.FeatureOccurrence enclosingFeatureOccurence = getEnclosingFeatureOccurence(featureOccurrence);
        return enclosingFeatureOccurence != null ? getLevel(enclosingFeatureOccurence) + 1 : 0;
    }

    class LevelComparator implements Comparator<FeatureModule.FeatureOccurrence> {
        public int compare(FeatureModule.FeatureOccurrence occ1, FeatureModule.FeatureOccurrence occ2) {
            return Integer.compare(getLevel(occ1), getLevel(occ2));
        }
    }

    public HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines) {
        Configuration.REPORT_ONLY = true;
        Logger logger = new Logger();
        logger.setInfoStrms(new SilentStream());
        logger.setFailStrms(System.err);
        Path inputDir = Paths.get(Arguments.getFeatureCoPPDirectory());
        Path outputDir = Filesystem.genPath(inputDir + "_split");
        Path moduleDir = Filesystem.genPath(outputDir.toString(), "___FeatureCoPP_modules");
        Pattern requestExprPattern = Pattern.compile(".*");
        CPPAnalyzer cppAnalyzer = new CPPAnalyzer(logger, inputDir, outputDir, moduleDir, requestExprPattern);

        // We have to copy the file before analysis because FeatureCoPP deletes
        // files with unbalanced #ifdef blocks.
        String uniqueID = UUID.randomUUID().toString();
        Path newFilePath = Paths.get(filePath + "_" + uniqueID);
        try {
            Files.copy(Paths.get(filePath), newFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Now we can analyze the copied file and remove it.
        try {
            cppAnalyzer.process(newFilePath);
        } finally {
            try {
                Files.delete(newFilePath);
            } catch (NoSuchFileException ignored) {
                // already removed by FeatureCoPP
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        HashMap<String, FeatureModule> featureTable = getFeatureTable();
        ArrayList<FeatureModule.FeatureOccurrence> allFeatureOccurences = new ArrayList<>();

        // A feature occurence models a block of #ifdef variability. It spans multiple lines and
        // can contain other feature occurences. First we flatten all feature occurences.
        for (Map.Entry<String, FeatureModule> entry : featureTable.entrySet())
            allFeatureOccurences.addAll(getFeatureOccurences(entry.getValue()));

        HashMap<Integer, PresenceCondition> locatedPresenceConditions = new HashMap<>();
        for (int line : lines)
            locatedPresenceConditions.put(line, FeatureCoPPPresenceCondition.getNotFound(line));
        Set<Integer> linesSet = Arrays.stream(lines).boxed().collect(Collectors.toSet());

        // Now we save presence conditions. Because nested occurences can override the presence
        // conditions of outer occurences, we first sort by the nesting level.
        allFeatureOccurences.stream().sorted(new LevelComparator()).forEach(featureOccurrence -> {
            for (int line = featureOccurrence.getBeginLine(); line <= featureOccurrence.getEndLine(); line++)
                if (linesSet.contains(line)) {
                    FeatureCoPPPresenceCondition presenceCondition =
                            new FeatureCoPPPresenceCondition(getNestedFeatureTree(featureOccurrence));
                    presenceCondition.history(line)
                            .include(locatedPresenceConditions.get(line))
                            .add(!locatedPresenceConditions.get(line).isPresent()
                                    ? "This presence condition has been located by FeatureCoPP."
                                    : "Because of nested conditionals, the presence condition has been refined by FeatureCoPP.", presenceCondition);
                    locatedPresenceConditions.put(line, presenceCondition);
                }
        });

        return locatedPresenceConditions;
    }
}

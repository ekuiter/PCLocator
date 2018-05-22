package de.ovgu.spldev.pclocator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MergePresenceConditionLocator extends PresenceConditionLocator {
    PresenceConditionLocator typeChef, superC, featureCoPP;
    protected Measurement _lastMeasurement;

    public MergePresenceConditionLocator(PresenceConditionLocator typeChef, PresenceConditionLocator superC, PresenceConditionLocator featureCoPP) {
        this.typeChef = typeChef;
        this.superC = superC;
        this.featureCoPP = featureCoPP;
    }

    public String getName() {
        return "Merge";
    }

    public HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines) {
        HashMap<Integer, PresenceCondition> locatedPresenceConditions = new HashMap<>(),
                typeChefPresenceConditions, superCPresenceConditions, featureCoPPPresenceConditions;

        // Locate presence conditions with TypeChef, SuperC and FeatureCoPP.
        // These are independent tasks which can be parallelized.
        ExecutorService executor = Executors.newCachedThreadPool();
        ArrayList<Callable<HashMap<Integer, PresenceCondition>>> tasks = new ArrayList<>();
        Function<PresenceConditionLocator, Callable<HashMap<Integer, PresenceCondition>>> locatePresenceConditionsTasks =
                presenceConditionLocator -> () -> {
                    try {
                        return presenceConditionLocator.locatePresenceConditions(filePath);
                    } catch (Exception e) {
                        Log.error("%s", e);
                    }
                    return new HashMap<>();
                };

        tasks.add(locatePresenceConditionsTasks.apply(typeChef));
        tasks.add(locatePresenceConditionsTasks.apply(superC));
        tasks.add(locatePresenceConditionsTasks.apply(featureCoPP));

        Measurement begin = new Measurement();
        try {
            List<Future<HashMap<Integer, PresenceCondition>>> futures = executor.invokeAll(tasks);
            typeChefPresenceConditions = futures.get(0).get();
            superCPresenceConditions = futures.get(1).get();
            featureCoPPPresenceConditions = futures.get(1).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
        _lastMeasurement = new Measurement().difference(begin);

        // For each line, choose the most appropriate presence condition.
        // To determine it, we make the following assumptions/trade-offs:
        // (a) A presence condition is always better than no presence condition (even if it is wrong).
        // (b) We prefer results from TypeChef and SuperC over FeatureCoPP because the former analyze
        //     #defines, #undefs etc. properly and also consider included files. FeatureCoPP only does
        //     a simple lexical analysis to determine the result and does not parse the actual C code,
        //     which makes it a very reliable fallback.
        // (c) If a presence condition A implies another presence condition B, then B is a specialization
        //     and yields less configurations. We argue that B is the better choice (although it limits
        //     the configuration space) whenever we are interested in deriving some configuration that
        //     includes the line with high confidence, like in the SPLC challenge.
        //     Note that this way we trade accuracy of the presence condition for a confident configuration.
        //     In the special case that TypeChef and SuperC locate equivalent presence conditions,
        //     we prefer SuperC because non-Boolean constraints might occur. Equivalence also
        //     means that the presence condition is likely to be accurate.
        for (int line : lines) {
            PresenceCondition typeChefPresenceCondition = typeChefPresenceConditions.getOrDefault(line, PresenceCondition.getNotFound(line)),
                    superCPresenceCondition = superCPresenceConditions.getOrDefault(line, PresenceCondition.getNotFound(line)),
                    featureCoPPPresenceCondition = featureCoPPPresenceConditions.getOrDefault(line, PresenceCondition.getNotFound(line));
            PresenceCondition presenceCondition = null;
            String explanation = "",
                    featureCoPPIgnoredExplanation = featureCoPPPresenceCondition.isPresent()
                    ? " FeatureCoPP has been ignored because TypeChef and SuperC deliver more accurate results." : "";

            // Now, we handle every possible combination of parser results and give suitable explanations:

            // no parser located a presence condition, give up
            if (!typeChefPresenceCondition.isPresent() && !superCPresenceCondition.isPresent() && !featureCoPPPresenceCondition.isPresent())
                presenceCondition = TypeChefPresenceCondition.getNotFound(line);

            // exactly one parser located a presence condition - see (a) above
            else if (typeChefPresenceCondition.isPresent() && !superCPresenceCondition.isPresent() && !featureCoPPPresenceCondition.isPresent())
                presenceCondition = typeChefPresenceCondition;
            else if (!typeChefPresenceCondition.isPresent() && superCPresenceCondition.isPresent() && !featureCoPPPresenceCondition.isPresent())
                presenceCondition = superCPresenceCondition;
            else if (!typeChefPresenceCondition.isPresent() && !superCPresenceCondition.isPresent() && featureCoPPPresenceCondition.isPresent())
                presenceCondition = featureCoPPPresenceCondition;

            // 2 or 3 parsers located a presence condition - from (b) follows that we can ignore FeatureCoPP
            else if (!superCPresenceCondition.isPresent()) {
                presenceCondition = typeChefPresenceCondition;
                explanation = "TypeChef has been preferred over FeatureCoPP because it delivers more accurate results.";
            } else if (!typeChefPresenceCondition.isPresent()) {
                presenceCondition = superCPresenceCondition;
                explanation = "SuperC has been preferred over FeatureCoPP because it delivers more accurate results.";
            }

            // both TypeChef and SuperC have located a presence condition, possibly also FeatureCoPP
            else {
                TypeChefPresenceCondition _typeChefPresenceCondition = (TypeChefPresenceCondition)
                        typeChefPresenceConditions.getOrDefault(line, TypeChefPresenceCondition.getNotFound(line)),
                        _superCPresenceCondition = (TypeChefPresenceCondition)
                                superCPresenceConditions.getOrDefault(line, TypeChefPresenceCondition.getNotFound(line));

                // compare the results from TypeChef and SuperC and use the more constrained one - see (c) above
                if (_typeChefPresenceCondition.equivalentTo(_superCPresenceCondition)) {
                    presenceCondition = superCPresenceCondition;
                    explanation = "TypeChef and SuperC located equivalent presence conditions. SuperC has been " +
                            "preferred because it may also contain non-Boolean sub-expressions." + featureCoPPIgnoredExplanation;
                } else if (_typeChefPresenceCondition.implies(_superCPresenceCondition)) {
                    presenceCondition = superCPresenceCondition;
                    explanation = "SuperC located a more constrained presence condition than TypeChef. SuperC has been " +
                            "preferred because it delivered a configuration sub-space consistent with TypeChef. " +
                            "The result may not include every satisfying configuration." + featureCoPPIgnoredExplanation;
                } else if (_superCPresenceCondition.implies(_typeChefPresenceCondition)) {
                    presenceCondition = typeChefPresenceCondition;
                    explanation = "TypeChef located a more constrained presence condition than SuperC. TypeChef has been " +
                            "preferred because it delivered a configuration sub-space consistent with SuperC. " +
                            "The result may not include every satisfying configuration." + featureCoPPIgnoredExplanation;
                }

                // if no presence condition implies the other, there is no easy way of determining a good
                // result and chances are high that the results from TypeChef and SuperC are not accurate.
                // Therefore we fall back to FeatureCoPP if possible (it usually is). Otherwise we use
                // SuperC because it handles non-Boolean constraints and more edge cases than TypeChef.
                else if (featureCoPPPresenceCondition.isPresent()) {
                    explanation = "TypeChef and SuperC located inconsistent presence conditions. " +
                            "Falling back to FeatureCoPP." + featureCoPPIgnoredExplanation;
                    presenceCondition = featureCoPPPresenceCondition;
                } else {
                    presenceCondition = superCPresenceCondition;
                    explanation = "TypeChef and SuperC located inconsistent presence conditions. SuperC has been " +
                            "preferred because it may also contain non-Boolean sub-expressions." + featureCoPPIgnoredExplanation;
                }
            }

            // Every combination of parser results is covered, presenceCondition now holds the merged presence condition.

            ArrayList<String> presentPresenceConditionLocators = new ArrayList<>();
            if (typeChefPresenceCondition.isPresent())
                presentPresenceConditionLocators.add("TypeChef");
            if (superCPresenceCondition.isPresent())
                presentPresenceConditionLocators.add("SuperC");
            if (featureCoPPPresenceCondition.isPresent())
                presentPresenceConditionLocators.add("FeatureCoPP");
            String summary = (presentPresenceConditionLocators.size() > 0
                ? String.join(", ", presentPresenceConditionLocators)
                    : "No parser") + " located a presence condition. ";

            assert presenceCondition != null;
            presenceCondition = presenceCondition.clone();
            presenceCondition.history()
                    .add("For locating this presence condition, the results from TypeChef, SuperC and FeatureCoPP have been merged. " +
                            "TypeChef located the following presence condition:")
                    .reference(typeChefPresenceCondition)
                    .add("SuperC located the following presence condition:")
                    .reference(superCPresenceCondition)
                    .add("FeatureCoPP located the following presence condition:")
                    .reference(featureCoPPPresenceCondition)
                    .add(summary + explanation);

            locatedPresenceConditions.put(line, presenceCondition);
        }

        return locatedPresenceConditions;
    }

    protected HashMap<Integer, PresenceCondition> locatePresenceConditions(String _filePath) {
        final String filePath = validateFilePath(_filePath);
        try (Stream<String> lineContentsStream = Files.lines(Paths.get(filePath))) {
            String[] lineContents = lineContentsStream.toArray(String[]::new);
            int[] lines = IntStream.rangeClosed(1, lineContents.length).toArray();
            return locatePresenceConditions(filePath, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Measurement getLastMeasurement() {
        return _lastMeasurement;
    }
}

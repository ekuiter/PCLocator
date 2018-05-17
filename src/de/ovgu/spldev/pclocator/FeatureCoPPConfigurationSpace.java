package de.ovgu.spldev.pclocator;

import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import de.ovgu.spldev.featurecopp.splmodel.FeatureTree;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import scala.Tuple3;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;

import java.util.*;
import java.util.function.Function;

public class FeatureCoPPConfigurationSpace extends ConfigurationSpace {
    private FeatureCoPPPresenceCondition presenceCondition;
    private String timeLimit;

    public FeatureCoPPConfigurationSpace(FeatureCoPPPresenceCondition featureCoPPPresenceCondition, String dimacsFilePath, String timeLimit) {
        super(dimacsFilePath);
        presenceCondition = featureCoPPPresenceCondition;
        this.timeLimit = timeLimit;
    }

    class ConfigurationIterator implements Iterator<Configuration> {
        private HashMap<String, IntVar> macros = new HashMap<>();
        Solver solver;
        boolean hasNext;

        ConfigurationIterator() {
            FeatureTree featureTree = presenceCondition.getFeatureTree();
            Model model = new Model(featureTree.toString());

            try {
                featureTree.makeCSP(model, macros).ne(0).post();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<SingleFeatureExpr> interestingFeatures = PresenceCondition.getInterestingFeatures(dimacsFilePath);
            for (SingleFeatureExpr interestingFeature : interestingFeatures) {
                IntVar var = macros.get(interestingFeature.feature());
                if (var != null)
                    try {
                        // "interesting features" in the DIMACS file should be Boolean
                        var.updateBounds(0, 1, null);
                    } catch (ContradictionException e) {
                        throw new RuntimeException(e);
                    }
            }

            postFeatureModel(dimacsFilePath, model);

            solver = model.getSolver();
            if (timeLimit != null)
                solver.limitTime(timeLimit);
            hasNext = solver.solve();
        }

        private void postFeatureModel(String dimacsFilePath, Model model) {
            Tuple3<scala.collection.immutable.Map<String, Object>,
                    List<scala.collection.immutable.List<Object>>,
                    Object>
                    dimacsData = PresenceCondition.loadDimacsData(dimacsFilePath);

            HashMap<Integer, IntVar> dimacsToChocoVar = new HashMap<>();
            Function<? super Object, ?> literalToVar = literal -> dimacsToChocoVar.get(Math.abs((int) literal));

            // declare every feature occuring in the DIMACS file
            JavaConverters.mapAsJavaMap(dimacsData._1()).forEach((feature, varID) -> {
                IntVar var = macros.get(feature);
                if (var == null) { // declare new feature
                    var = model.boolVar();
                } else { // convert existing IntVar to BoolVar so we can add BoolVar[] literals below
                    BoolVar newVar = model.boolVar();
                    model.allEqual(var, newVar).post();
                    var = newVar;
                }
                macros.put(feature, var);
                dimacsToChocoVar.put((Integer) varID, var);
            });

            // post clauses occuring in the DIMACS file
            JavaConverters.asJavaCollection(dimacsData._2()).forEach(_clause -> {
                Collection<Object> clause = JavaConverters.asJavaCollection(_clause);
                BoolVar[] posLits = clause.stream().filter(literal -> (int) literal > 0).map(literalToVar).toArray(BoolVar[]::new);
                BoolVar[] negLits = clause.stream().filter(literal -> (int) literal < 0).map(literalToVar).toArray(BoolVar[]::new);
                model.addClauses(posLits, negLits);
            });
        }

        public boolean hasNext() {
            return hasNext;
        }

        public Configuration next() {
            if (!hasNext())
                throw new NoSuchElementException();

            Configuration configuration = new FeatureCoPPConfiguration(macros);
            hasNext = solver.solve();
            return configuration;
        }
    }

    public Iterator<Configuration> iterator() {
        return new ConfigurationIterator();
    }
}

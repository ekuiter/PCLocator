package de.ovgu.spldev.pclocator;

import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.featureexpr.FeatureModelFactory;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import de.fosd.typechef.lexer.FeatureExprLib;
import scala.Function1;
import scala.Tuple3;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.io.Source;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

public class DimacsFileReader {
    private static FeatureModelFactory featureModelFactory = FeatureExprLib.featureModelFactory();
    private static Function1<String, String> translateNames = name -> name;
    private static boolean autoAddVariables = false;
    private String dimacsFilePath;

    public DimacsFileReader(String dimacsFilePath) {
        this.dimacsFilePath = dimacsFilePath;
    }

    private Source getDimacsFileSource() {
        return Source.fromFile(dimacsFilePath, "UTF-8");
    }

    public FeatureModel getFeatureModel() {
        return featureModelFactory.createFromDimacsFile(getDimacsFileSource(), translateNames, autoAddVariables);
    }

    public Tuple3<Map<String, Object>, List<List<Object>>, Object> loadDimacsData() {
        try {
            // We need to pass "interesting features" to getSatisfiableAssignment below.
            // We assume these are the features used in the DIMACS file. To get these,
            // we call loadDimacsData which is protected, so first we make it accessible.
            Method loadDimacsDataMethod = FeatureModelFactory.class.getDeclaredMethod(
                    "loadDimacsData", Source.class, Function1.class, boolean.class);
            return (Tuple3<Map<String, Object>, List<List<Object>>, Object>)
                    loadDimacsDataMethod.invoke(featureModelFactory, getDimacsFileSource(), translateNames, autoAddVariables);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public Set<SingleFeatureExpr> getInterestingFeatures() {
        return JavaConverters
                .mapAsJavaMap(loadDimacsData()._1())
                .keySet() // get variables (= features)
                .stream()
                .map(FeatureExprFactory::createDefinedExternal) // convert to SingleFeatureExpr
                .collect(Collectors.toSet());
    }
}

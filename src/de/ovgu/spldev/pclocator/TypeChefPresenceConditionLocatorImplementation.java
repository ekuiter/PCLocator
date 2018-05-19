package de.ovgu.spldev.pclocator;

import de.fosd.typechef.conditional.Conditional;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.FeatureExprLib;
import de.fosd.typechef.lexer.LexerException;
import de.fosd.typechef.lexer.LexerFrontend;
import de.fosd.typechef.options.FrontendOptions;
import de.fosd.typechef.options.FrontendOptionsWithConfigFiles;
import de.fosd.typechef.parser.TokenReader;
import de.fosd.typechef.parser.c.*;
import scala.Product;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;
import scala.reflect.ClassTag;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TypeChefPresenceConditionLocatorImplementation implements PresenceConditionLocator.Implementation, EnforceTreeHelper, ASTNavigation {
    private PresenceConditionLocator.Options options;

    public String getName() {
        return "TypeChef";
    }

    public void setOptions(PresenceConditionLocator.Options options) {
        this.options = options;
    }

    public de.ovgu.spldev.pclocator.PresenceCondition getTrue() {
        return TypeChefPresenceCondition.TRUE;
    }

    public PresenceCondition fromDNF(String formula) {
        return TypeChefPresenceCondition.fromDNF(formula);
    }

    private String getPostIncludes() {
        if (options == null || options.getIncludeDirectories() == null)
            return "";
        return String.join(",", options.getIncludeDirectories());
    }

    private TokenReader<CToken, CTypeContext> lex(FrontendOptions opt) {
        try {
            Conditional<LexerFrontend.LexerResult> tokens = new LexerFrontend().run(opt, true);
            return CLexerAdapter.prepareTokens(tokens);
        } catch (LexerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TranslationUnit parse(FrontendOptions opt, FeatureModel featureModel) {
        ParserMain parserMain = new ParserMain(new CParser(featureModel, false));
        lex(opt);
        TranslationUnit ast = parserMain.parserMain(lex(opt), opt, featureModel);
        if (ast == null)
            throw new RuntimeException("TypeChef parser failed");
        return prepareAST(ast);
    }

    private ASTEnv createASTEnv(Product ast) {
        Set<FeatureExpr> _fexpset = new HashSet<>();
        _fexpset.add(FeatureExprFactory.True());
        scala.collection.immutable.Set<FeatureExpr> fexpset =
                JavaConverters.asScalaSet(_fexpset).toSet();
        return CASTEnv.createASTEnv(ast, fexpset);
    }

    private Collection<AST> getAllASTElems(AST ast) {
        List<AST> astElems = filterAllASTElems(ast, ClassTag.apply(AST.class));
        return JavaConverters.asJavaCollection(astElems);
    }

    public HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines) {
        HashMap<Integer, PresenceCondition> locatedPresenceConditions = new HashMap<>();
        for (int line : lines)
            locatedPresenceConditions.put(line, TypeChefPresenceCondition.NOT_FOUND);
        PrintStream out = System.out;

        System.setOut(new SilentStream());

        TranslationUnit ast = null;
        try {
            String[] args = {"--postIncludes=" + getPostIncludes(), filePath};
            FrontendOptionsWithConfigFiles opt = new FrontendOptionsWithConfigFiles();
            opt.parseOptions(args);
            ast = parse(opt, FeatureExprLib.featureModelFactory().empty());
        } finally {
            System.setOut(out);
        }

        // to avoid scanning the AST multiple times,
        // we will advance simultaneously in the AST and the lines
        // (assuming both are sorted)
        ASTEnv env = createASTEnv(ast);
        LineSupplier lineSupplier = new LineSupplier(lines, locatedPresenceConditions);
        int line = lineSupplier.next();

        if (line != -1)
            for (AST elem : getAllASTElems(ast))
                if (elem.hasPosition()) {
                    while (line < elem.getPositionFrom().getLine())
                        if ((line = lineSupplier.next()) == -1)
                            break;
                    if (elem.getPositionFrom().getLine() == line) {
                        FeatureExpr featureExpr = env.featureExpr(elem);
                        locatedPresenceConditions.put(line,
                                featureExpr.isTautology()
                                        ? TypeChefPresenceCondition.TRUE
                                        : new TypeChefPresenceCondition(featureExpr));
                        if ((line = lineSupplier.next()) == -1)
                            break;
                    }
                }

        while (lineSupplier.next() != -1) ;
        return locatedPresenceConditions;
    }
}

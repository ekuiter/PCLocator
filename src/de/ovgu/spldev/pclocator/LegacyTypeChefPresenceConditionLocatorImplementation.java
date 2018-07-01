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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class LegacyTypeChefPresenceConditionLocatorImplementation implements PresenceConditionLocator.Implementation, EnforceTreeHelper, ASTNavigation {
    protected HashMap<Integer, de.ovgu.spldev.pclocator.PresenceCondition> _locatedPresenceConditions;
    protected PresenceConditionLocator.Options options;
    protected String arguments;
    protected Path _filePath;

    public String getName() {
        return "TypeChef (legacy)";
    }

    public PresenceConditionLocator.Options getOptions() {
        return options;
    }

    public void setOptions(PresenceConditionLocator.Options options) {
        this.options = options;
    }

    public de.ovgu.spldev.pclocator.PresenceCondition getTrue() {
        return TypeChefPresenceCondition.getTrue();
    }

    public PresenceCondition[] fromDNF(String formula) {
        return new PresenceCondition[]{TypeChefPresenceCondition.fromDNF(formula)};
    }

    private String getPreIncludes() {
        if (options == null || options.getIncludeDirectories() == null)
            return "";
        return String.join(",", options.getIncludeDirectories());
    }

    protected TokenReader<CToken, CTypeContext> lex(FrontendOptions opt) {
        try {
            Conditional<LexerFrontend.LexerResult> tokens = new LexerFrontend().run(opt, true);
            return CLexerAdapter.prepareTokens(tokens);
        } catch (LexerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TranslationUnit parse(FrontendOptions opt, FeatureModel featureModel) {
        ParserMain parserMain = new ParserMain(new CParser(featureModel, false));
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

    protected void putPresenceCondition(int line, FeatureExpr featureExpr) {
        TypeChefPresenceCondition presenceCondition = featureExpr.isTautology()
                ? TypeChefPresenceCondition.getTrue()
                : new TypeChefPresenceCondition(featureExpr);
        presenceCondition.history(line)
                .include(_locatedPresenceConditions.get(line))
                .add("This presence condition has been located by TypeChef using the following arguments: %s", arguments);
        _locatedPresenceConditions.put(line, presenceCondition);
    }

    protected Object run(FrontendOptionsWithConfigFiles opt) throws InterruptedException, ExecutionException {
        // Execute TypeChef in a separate thread because it uses a recursive descent parser
        // which might stack-overflow for complex files (e.g., Busybox).
        // If you encounter a stack overflow, consider using -Xss... to increase the stack size.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TranslationUnit> future = executor.submit(
                () -> parse(opt, FeatureExprLib.featureModelFactory().empty()));
        executor.shutdown();
        return future.get();
    }

    protected void process(Object artifact, LineSupplier lineSupplier) {
        // to avoid scanning the AST multiple times,
        // we will advance simultaneously in the AST and the lines
        // (assuming both are sorted)
        AST ast = (AST) artifact;
        ASTEnv env = createASTEnv(ast);
        int line = lineSupplier.next();

        if (line != -1)
            for (AST elem : getAllASTElems(ast)) {
                if (elem.hasPosition() && elem.getPositionFrom().getFile().equals("file " + _filePath.toString())) {
                    line = lineSupplier.catchUp(elem.getPositionFrom().getLine());
                    if (elem.getPositionFrom().getLine() == line) {
                        putPresenceCondition(line, env.featureExpr(elem));
                        if ((line = lineSupplier.next()) == -1)
                            break;
                    }
                }
            }

        while (lineSupplier.next() != -1) ;
    }

    public HashMap<Integer, PresenceCondition> locatePresenceConditions(String filePath, int[] lines) {
        _filePath = Paths.get(filePath).toAbsolutePath().normalize();
        _locatedPresenceConditions = new HashMap<>();
        PrintStream out = System.out;
        System.setOut(new SilentStream());

        Object artifact;
        try {
            ArrayList<String> args = new ArrayList<>();
            args.add("--preIncludes=" + getPreIncludes());
            if (options.getPlatformHeaderFilePath() != null) {
                args.add("-h");
                args.add(options.getPlatformHeaderFilePath());
            } else
                Arguments.warnPlatformHeader();
            args.add(_filePath.toString());
            FrontendOptionsWithConfigFiles opt = new FrontendOptionsWithConfigFiles();
            opt.parseOptions(args.toArray(new String[0]));
            arguments = String.join(" ", args);
            artifact = run(opt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(out);
        }

        process(artifact, new LineSupplier(lines, _locatedPresenceConditions));

        HashMap<Integer, de.ovgu.spldev.pclocator.PresenceCondition> locatedPresenceConditions =
                _locatedPresenceConditions;
        _locatedPresenceConditions = null;

        return locatedPresenceConditions;
    }
}

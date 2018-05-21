package xtc.lang.cpp;

import xtc.parser.ParseException;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.Tool;

import java.io.*;
import java.util.*;

/**
 * A simplified version of the SuperC parser.
 */
public class SuperCLite extends Tool {
    private final SuperCPresenceConditionLocatorImplementation _superCPresenceConditionLocatorImplementation;
    /**
     * The user defined include paths
     */
    List<String> I;

    /**
     * Additional paths for quoted header file names
     */
    List<String> iquote;

    /**
     * Additional paths for system headers
     */
    List<String> sysdirs;

    StringReader commandline;

    /**
     * Preprocessor support for token-creation.
     */
    private final static TokenCreator tokenCreator = new CTokenCreator();

    /**
     * Simplify nested conditionals in preprocessor output.
     */
    private final static boolean SIMPLIFY_NESTED_CONDITIONALS = true;

    /**
     * Create a new tool.
     */
    public SuperCLite(SuperCPresenceConditionLocatorImplementation superCPresenceConditionLocatorImplementation) {
        _superCPresenceConditionLocatorImplementation = superCPresenceConditionLocatorImplementation;
    }

    /**
     * Return the name of this object.
     *
     * @return The name of this object.
     */
    public String getName() {
        return "SuperCLite";
    }

    public void init() {
        super.init();

        runtime.
                // Regular preprocessor arguments.
                        word("I", "I", true,
                        "Add a directory to the header file search path.").
                word("isystem", "isystem", true,
                        "Add a system directory to the header file search path.").
                word("iquote", "iquote", true,
                        "Add a quote directory to the header file search path.").
                bool("nostdinc", "nostdinc", false,
                        "Don't use the standard include paths.").
                bool("nobuiltins", "nobuiltins", false,
                        "Disable gcc built-in macros.").
                word("D", "D", true, "Define a macro.").
                word("U", "U", true, "Undefine a macro.  Occurs after all -D arguments "
                        + "which is a departure from gnu cpp.").
                word("include", "include", true, "Include a header.").

                // Output and debugging
                        bool("showErrors", "showErrors", false,
                        "Emit preprocessing and parsing errors to standard err.").
                word("locatePresenceCondition", "locatePresenceCondition", true,
                        "Locate a line of code in the AST.")
        ;
    }

    /**
     * Prepare for file processing.  Build header search paths.
     * Include command-line headers. Process command-line and built-in macros.
     */
    public void prepare() {
        // Set the command-line argument defaults.
        runtime.initDefaultValues();

        // Use the Java implementation of JavaBDD. Setting it here means
        // the user doesn't have to set it on the commandline.
        System.setProperty("bdd", "java");

        // Get preprocessor settings.
        iquote = new LinkedList<String>();
        I = new LinkedList<String>();
        sysdirs = new LinkedList<String>();

        // The following shows which command-line options add to ""
        // headers and which add to <> headers.  Additionally, only
        // -isystem are considered system headers.  System headers have a
        // special marker to cpp, but SuperC does not need to use this.

        // currentheaderdirectory iquote I    isystem standardsystem
        // ""                     ""     ""   ""     ""
        //                               <>   <>     <>
        //                                    marked system headers
        if (!runtime.test("nostdinc")) {
            for (int i = 0; i < Builtins.sysdirs.length; i++) {
                sysdirs.add(Builtins.sysdirs[i]);
            }
        }

        for (Object o : runtime.getList("isystem")) {
            if (o instanceof String) {
                String s;

                s = (String) o;
                if (sysdirs.indexOf(s) < 0) {
                    sysdirs.add(s);
                }
            }
        }

        for (Object o : runtime.getList("I")) {
            if (o instanceof String) {
                String s;

                s = (String) o;

                // Ignore I if already a system path.
                if (sysdirs.indexOf(s) < 0) {
                    I.add(s);
                }
            }
        }

        for (Object o : runtime.getList("iquote")) {
            if (o instanceof String) {
                String s;

                s = (String) o;
                // cpp permits bracket and quote search chains to have
                // duplicate dirs.
                if (iquote.indexOf(s) < 0) {
                    iquote.add(s);
                }
            }
        }

        // Make one large file for command-line/builtin stuff.
        StringBuilder commandlinesb;

        commandlinesb = new StringBuilder();

        if (! runtime.test("nobuiltins")) {
            commandlinesb.append(Builtins.builtin);
        }

        for (Object o : runtime.getList("D")) {
            if (o instanceof String) {
                String s, name, definition;

                s = (String) o;

                // Truncate at first newline according to gcc spec.
                if (s.indexOf("\n") >= 0) {
                    s = s.substring(0, s.indexOf("\n"));
                }
                if (s.indexOf("=") >= 0) {
                    name = s.substring(0, s.indexOf("="));
                    definition = s.substring(s.indexOf("=") + 1);
                }
                else {
                    name = s;
                    // The default for command-line defined guard macros.
                    definition = "1";
                }
                commandlinesb.append("#define " + name + " " + definition + "\n");
            }
        }

        for (Object o : runtime.getList("U")) {
            if (o instanceof String) {
                String s, name, definition;

                s = (String) o;
                // Truncate at first newline according to gcc spec.
                if (s.indexOf("\n") >= 0) {
                    s = s.substring(0, s.indexOf("\n"));
                }
                name = s;
                commandlinesb.append("#undef " + name + "\n");
            }
        }

        for (Object o : runtime.getList("include")) {
            if (o instanceof String) {
                String filename;

                filename = (String) o;
                commandlinesb.append("#include \"" + filename + "\"\n");
            }
        }

        if (commandlinesb.length() > 0) {
            commandline = new StringReader(commandlinesb.toString());

        } else {
            commandline = null;
        }
    }

    public Node parse(Reader in, File file) throws IOException, ParseException {
        HeaderFileManager fileManager;
        MacroTable macroTable;
        PresenceConditionManager presenceConditionManager;
        ExpressionParser expressionParser;
        ConditionEvaluator conditionEvaluator;
        Iterator<Syntax> preprocessor;
        Node result = null;

        // Initialize the preprocessor with built-ins and command-line
        // macros and includes.
        macroTable = new MacroTable(tokenCreator);
        presenceConditionManager = new PresenceConditionManager();
        expressionParser = ExpressionParser.fromRats();
        conditionEvaluator = new ConditionEvaluator(expressionParser,
                presenceConditionManager,
                macroTable);

        if (null != commandline) {
            Syntax syntax;

            try {
                commandline.reset();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            fileManager = new HeaderFileManager(commandline,
                    new File("<command-line>"),
                    iquote, I, sysdirs, tokenCreator, null);
            fileManager.showErrors(runtime.test("showErrors"));

            preprocessor = new Preprocessor(fileManager,
                    macroTable,
                    presenceConditionManager,
                    conditionEvaluator,
                    tokenCreator);

            ((Preprocessor) preprocessor)
                    .showErrors(runtime.test("showErrors"));

            do {
                syntax = preprocessor.next();
            } while (syntax.kind() != Syntax.Kind.EOF);

            commandline = null;
        }

        fileManager = new HeaderFileManager(in, file, iquote, I, sysdirs,
                tokenCreator, null,
                runtime.getString(xtc.util.Runtime.INPUT_ENCODING));
        fileManager.showErrors(runtime.test("showErrors"));

        preprocessor = new Preprocessor(fileManager,
                macroTable,
                presenceConditionManager,
                conditionEvaluator,
                tokenCreator);

        ((Preprocessor) preprocessor)
                .showErrors(runtime.test("showErrors"));

        // Run the SuperC preprocessor and parser.
        ForkMergeParser parser;
        Object translationUnit;

        // Only pass ordinary tokens and conditionals to the parser.
        preprocessor = new TokenFilter(preprocessor);

        // Create a new semantic values class for C.
        SemanticValues semanticValues = CSemanticValues.getInstance();
        CSemanticActions actions = CSemanticActions.getInstance();
        CParsingContext initialParsingContext = new CParsingContext();

        parser = new ForkMergeParser(CParseTables.getInstance(), semanticValues,
                actions, initialParsingContext,
                preprocessor, presenceConditionManager);
        parser.saveLayoutTokens(true);
        parser.setSharedReductions(true);
        parser.setEarlyReduce(true);
        parser.setFollowSetCaching(true);
        parser.showErrors(runtime.test("showErrors"));

        translationUnit = parser.parse();

        if (null != translationUnit
                && !((Node) translationUnit).getName().equals("TranslationUnit")) {
            GNode tu = GNode.create("TranslationUnit");
            tu.add(translationUnit);
            translationUnit = tu;
        }

        initialParsingContext.free();

        for (Object o : runtime.getList("locatePresenceCondition")) {
            if (o instanceof String) {
                int line = Integer.parseInt((String) o);
                _superCPresenceConditionLocatorImplementation.locatePresenceCondition(
                        (Node) translationUnit, line, presenceConditionManager);
            }
        }

        result = (Node) translationUnit;

        return result;
    }
}

package xtc.lang.cpp;

import de.ovgu.spldev.pclocator.Arguments;
import de.ovgu.spldev.pclocator.PresenceConditionLocator;
import de.ovgu.spldev.pclocator.TrackErrorStream;
import xtc.lang.cpp.PresenceConditionManager.PresenceCondition;
import xtc.parser.ParseException;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class SuperCPresenceConditionLocatorImplementation implements PresenceConditionLocator.Implementation {
    private HashMap<Integer, de.ovgu.spldev.pclocator.PresenceCondition> _locatedPresenceConditions;
    private PresenceConditionLocator.Options options;
    private String arguments;
    private Path _filePath;

    public String getName() {
        return "SuperC";
    }

    public PresenceConditionLocator.Options getOptions() {
        return options;
    }

    public void setOptions(PresenceConditionLocator.Options options) {
        this.options = options;
    }

    public de.ovgu.spldev.pclocator.PresenceCondition getTrue() {
        return SuperCPresenceCondition.getTrue();
    }

    public de.ovgu.spldev.pclocator.PresenceCondition fromDNF(String formula) {
        return SuperCPresenceCondition.fromDNF(formula);
    }

    private void addIncludeDirectories(ArrayList<String> args) {
        if (options == null || options.getIncludeDirectories() == null)
            return;
        for (String includeDirectory : options.getIncludeDirectories()) {
            args.add("-I");
            args.add(includeDirectory);
        }
    }

    void locatePresenceCondition(
            Node translationUnit, int line, PresenceConditionManager presenceConditionManager) {
        PresenceCondition presenceCondition = _locatePresenceCondition(
                translationUnit, line, presenceConditionManager.new PresenceCondition(true));
        de.ovgu.spldev.pclocator.PresenceCondition superCPresenceCondition =
                presenceCondition == null
                        ? SuperCPresenceCondition.getNotFound(line)
                        : (presenceCondition.isTrue()
                        ? SuperCPresenceCondition.getTrue(line)
                        : new SuperCPresenceCondition(presenceCondition, presenceConditionManager));
        if (presenceCondition != null)
            superCPresenceCondition.history(line)
                    .include(_locatedPresenceConditions.get(line))
                    .add("This presence condition has been located by SuperC using the following arguments: %s", arguments);
        _locatedPresenceConditions.put(line, superCPresenceCondition);
    }

    private PresenceCondition _locatePresenceCondition(
            Node n, int line, PresenceCondition presenceCondition) {
        if (n == null)
            throw new ParseException("could not parse input file");
        else if (n.isToken()) {
            if (n.hasLocation() && n.getLocation().file.equals(_filePath.toString()) && n.getLocation().line == line)
                return presenceCondition;
        } else {
            if (n instanceof GNode
                    && ((GNode) n).hasName(ForkMergeParser.CHOICE_NODE_NAME)) {

                PresenceCondition branchCondition = null;

                for (Object bo : n) {
                    if (bo instanceof PresenceCondition) {
                        branchCondition = (PresenceCondition) bo;
                    } else if (bo instanceof Node) {
                        PresenceCondition ret = _locatePresenceCondition((Node) bo, line, branchCondition);
                        if (ret != null)
                            return ret;
                    }
                }

            } else {
                for (Object o : n) {
                    PresenceCondition ret = _locatePresenceCondition((Node) o, line, presenceCondition);
                    if (ret != null)
                        return ret;
                }
            }

        }

        return null;
    }

    private String[] buildArgs(int[] lines) {
        ArrayList<String> args = new ArrayList<>();
        args.add("-no-exit");
        args.add("-silent");
        args.add("-showErrors");
        args.add("-nobuiltins");
        if (options.getPlatformHeaderFilePath() != null) {
            args.add("-include");
            args.add(options.getPlatformHeaderFilePath());
        } else
            Arguments.warnPlatformHeader();
        addIncludeDirectories(args);
        for (int line : lines) {
            args.add("-pc");
            args.add(Integer.toString(line));
        }
        args.add(_filePath.toString());
        return args.toArray(new String[0]);
    }

    public HashMap<Integer, de.ovgu.spldev.pclocator.PresenceCondition> locatePresenceConditions(String filePath, int[] lines) {
        _filePath = Paths.get(filePath).toAbsolutePath().normalize();
        _locatedPresenceConditions = new HashMap<>();
        for (int line : lines)
            _locatedPresenceConditions.put(line, SuperCPresenceCondition.getNotFound(line));
        String[] args = buildArgs(lines);
        PrintStream err = System.err;
        TrackErrorStream trackErrorStream = new TrackErrorStream(System.err);
        System.setErr(trackErrorStream);

        try {
            SuperCLite superCLite = new SuperCLite(this);
            arguments = String.join(" ", args);
            superCLite.run(args);
        } finally {
            System.setErr(err);
        }

        if (trackErrorStream.seenError())
            throw new RuntimeException("SuperC parser failed");

        HashMap<Integer, de.ovgu.spldev.pclocator.PresenceCondition> locatedPresenceConditions =
                _locatedPresenceConditions;
        _locatedPresenceConditions = null;

        return locatedPresenceConditions;
    }
}

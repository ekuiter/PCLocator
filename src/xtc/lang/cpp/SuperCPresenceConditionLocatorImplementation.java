package xtc.lang.cpp;

import de.ovgu.spldev.pclocator.PresenceConditionLocator;
import de.ovgu.spldev.pclocator.TrackErrorStream;
import xtc.lang.cpp.PresenceConditionManager.PresenceCondition;
import xtc.parser.ParseException;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SuperCPresenceConditionLocatorImplementation implements PresenceConditionLocator.Implementation {
    private HashMap<Integer, de.ovgu.spldev.pclocator.PresenceCondition> _locatedPresenceConditions;
    private PresenceConditionLocator.Options options;

    public String getName() {
        return "SuperC";
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
                    .add("This presence condition has been located by SuperC.");
        _locatedPresenceConditions.put(line, superCPresenceCondition);
    }

    private PresenceCondition _locatePresenceCondition(
            Node n, int line, PresenceCondition presenceCondition) {
        if (n == null)
            throw new ParseException("could not parse input file");
        else if (n.isToken()) {
            if (n.hasLocation() && n.getLocation().line == line)
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

    private String[] buildArgs(String filePath, int[] lines) {
        ArrayList<String> args = new ArrayList<>();
        args.add("-no-exit");
        args.add("-silent");
        args.add("-showErrors");
        addIncludeDirectories(args);
        for (int line : lines) {
            args.add("-locatePresenceCondition");
            args.add(Integer.toString(line));
        }
        args.add(filePath);
        return args.toArray(new String[args.size()]);
    }

    public HashMap<Integer, de.ovgu.spldev.pclocator.PresenceCondition> locatePresenceConditions(String filePath, int[] lines) {
        _locatedPresenceConditions = new HashMap<>();
        for (int line : lines)
            _locatedPresenceConditions.put(line, SuperCPresenceCondition.getNotFound(line));
        PrintStream err = System.err;
        TrackErrorStream trackErrorStream = new TrackErrorStream(System.err);
        System.setErr(trackErrorStream);

        try {
            SuperCLite superCLite = new SuperCLite(this);
            superCLite.run(buildArgs(filePath, lines));
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

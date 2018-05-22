package de.ovgu.spldev.pclocator;

public abstract class PresenceCondition {
    abstract public String toString();

    abstract public boolean isPresent();

    abstract public PresenceCondition clone();

    abstract public ConfigurationSpace getSatisfyingConfigurationSpace(String dimacsFilePath, Integer limit, String timeLimit);

    protected static String notFoundHistory = "In the beginning, we start with no presence condition.";
    protected static String trueHistory = null;
    protected static String falseHistory = null;

    public static PresenceCondition getNotFound(Integer line) {
        PresenceCondition notFound = new PresenceCondition() {
            public String toString() {
                return "?";
            }

            public boolean isPresent() {
                return false;
            }

            public ConfigurationSpace getSatisfyingConfigurationSpace(String dimacsFilePath, Integer limit, String timeLimit) {
                return ConfigurationSpace.getNotFound(this);
            }

            public PresenceCondition clone() {
                return getNotFound(line);
            }
        };
        notFound.history(line).add(notFoundHistory);
        return notFound;
    }

    public static PresenceCondition getNotFound() {
        return getNotFound(null);
    }

    public void print(boolean isExplain) {
        if (isExplain)
            System.out.println(history());
        else if (!isPresent())
            Log.error("no presence condition found");
        else
            System.out.println(this);
    }

    public boolean compatible(PresenceCondition that) {
        return this instanceof TypeChefPresenceCondition && that instanceof TypeChefPresenceCondition ||
                this instanceof FeatureCoPPPresenceCondition && that instanceof FeatureCoPPPresenceCondition;
    }

    public PresenceCondition and(PresenceCondition that) {
        if (!isPresent() || !that.isPresent())
            return getNotFound(); // propagate any failures from the parser

        if (this instanceof TypeChefPresenceCondition && that instanceof TypeChefPresenceCondition)
            return ((TypeChefPresenceCondition) this).and((TypeChefPresenceCondition) that);
        else if (this instanceof FeatureCoPPPresenceCondition && that instanceof FeatureCoPPPresenceCondition)
            return ((FeatureCoPPPresenceCondition) this).and((FeatureCoPPPresenceCondition) that);
        else
            throw new RuntimeException("can not \"and\" constrain PCs which different types");
    }

    public Log.History history(Integer line) {
        return Log.history(this, line);
    }

    public Log.History history() {
        return Log.history(this);
    }

    public Integer getLine() {
        return ((Log.PresenceConditionHistory) history()).getLine();
    }
}

package de.ovgu.spldev.pclocator;

public abstract class PresenceCondition {
    abstract public String toString();

    abstract public boolean isPresent();

    abstract public ConfigurationSpace getSatisfyingConfigurationSpace(String dimacsFilePath, String timeLimit);

    public static PresenceCondition NOT_FOUND =
            new PresenceCondition() {
                public String toString() {
                    return "?";
                }

                public boolean isPresent() {
                    return false;
                }

                public ConfigurationSpace getSatisfyingConfigurationSpace(String dimacsFilePath, String timeLimit) {
                    return ConfigurationSpace.NOT_FOUND;
                }
            };

    public void print() {
        if (!isPresent())
            Log.error("no presence condition found");
        else
            System.out.println(this);
    }

    public PresenceCondition and(PresenceCondition that) {
        if (!isPresent() || !that.isPresent())
            return NOT_FOUND; // propagate any failures from the parser

        if (this instanceof TypeChefPresenceCondition && that instanceof TypeChefPresenceCondition)
            return ((TypeChefPresenceCondition) this).and((TypeChefPresenceCondition) that);
        else if (this instanceof FeatureCoPPPresenceCondition && that instanceof FeatureCoPPPresenceCondition)
            return ((FeatureCoPPPresenceCondition) this).and((FeatureCoPPPresenceCondition) that);
        else
            throw new RuntimeException("can not \"and\" constrain PCs which different types");
    }
}

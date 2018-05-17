package de.ovgu.spldev.pclocator;

import org.chocosolver.util.tools.TimeUtils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TypeChefConfigurationSpace extends ConfigurationSpace {
    private PresenceCondition initialPresenceCondition;
    private Long timeLimit;

    public TypeChefConfigurationSpace(PresenceCondition presenceCondition, String dimacsFilePath, String timeLimit) {
        super(dimacsFilePath);
        initialPresenceCondition = presenceCondition;
        this.timeLimit = timeLimit != null ? TimeUtils.convertInMilliseconds(timeLimit) : null;
    }

    class ConfigurationIterator implements Iterator<Configuration> {
        private PresenceCondition nextPresenceCondition = initialPresenceCondition;
        private TypeChefConfiguration nextTypeChefConfiguration = initialPresenceCondition.getSatisfyingConfiguration(dimacsFilePath);
        private Measurement begin = new Measurement();

        public boolean hasNext() {
            if (timeLimit != null && new Measurement().difference(begin).time > timeLimit)
                return false;
            return nextTypeChefConfiguration != TypeChefConfiguration.NOT_FOUND;
        }

        public Configuration next() {
            if (!hasNext())
                throw new NoSuchElementException();
            PresenceCondition currentPresenceCondition = nextPresenceCondition;
            TypeChefConfiguration currentTypeChefConfiguration = nextTypeChefConfiguration;
            nextPresenceCondition = currentPresenceCondition.and(currentTypeChefConfiguration.toPresenceCondition().not());
            nextTypeChefConfiguration = nextPresenceCondition.getSatisfyingConfiguration(dimacsFilePath);
            return currentTypeChefConfiguration;
        }
    }

    public Iterator<Configuration> iterator() {
        return new ConfigurationIterator();
    }
}

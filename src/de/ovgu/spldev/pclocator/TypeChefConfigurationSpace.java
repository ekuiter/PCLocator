package de.ovgu.spldev.pclocator;

import org.chocosolver.util.tools.TimeUtils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TypeChefConfigurationSpace extends ConfigurationSpace {
    private TypeChefPresenceCondition initialPresenceCondition;
    private Integer limit;
    private Long timeLimit;

    public TypeChefConfigurationSpace(TypeChefPresenceCondition presenceCondition, String dimacsFilePath, Integer limit, String timeLimit) {
        super(dimacsFilePath);
        initialPresenceCondition = presenceCondition;
        this.limit = limit;
        this.timeLimit = timeLimit != null ? TimeUtils.convertInMilliseconds(timeLimit) : null;
    }

    class ConfigurationIterator implements Iterator<Configuration> {
        private TypeChefPresenceCondition nextPresenceCondition = initialPresenceCondition;
        private TypeChefConfiguration nextTypeChefConfiguration = initialPresenceCondition.getSatisfyingConfiguration(dimacsFilePath);
        private Measurement begin = new Measurement();
        private int count = 0;

        public boolean hasNext() {
            if (timeLimit != null && new Measurement().difference(begin).time > timeLimit ||
                    limit != null && count >= limit)
                return false;
            return nextTypeChefConfiguration.isPresent();
        }

        public Configuration next() {
            if (!hasNext())
                throw new NoSuchElementException();
            TypeChefPresenceCondition currentPresenceCondition = nextPresenceCondition;
            TypeChefConfiguration currentConfiguration = nextTypeChefConfiguration;
            nextPresenceCondition = currentPresenceCondition.and(currentConfiguration.toPresenceCondition().not());
            nextTypeChefConfiguration = nextPresenceCondition.getSatisfyingConfiguration(dimacsFilePath);
            count++;
            return currentConfiguration;
        }
    }

    public Iterator<Configuration> iterator() {
        return new ConfigurationIterator();
    }
}

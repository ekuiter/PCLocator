package de.ovgu.spldev.pclocator;

public class IgnorePreprocessorPresenceConditionLocator extends MockSystemHeadersPresenceConditionLocator {
    boolean isFeatureCoPPImplementation = false;

    IgnorePreprocessorPresenceConditionLocator(Implementation implementation, Options options) {
        super(implementation, options);
        if (implementation instanceof FeatureCoPPPresenceConditionLocatorImplementation)
            isFeatureCoPPImplementation = true;
    }

    protected boolean lineNotAvailable(String lineContent) {
        if (isFeatureCoPPImplementation && !PreprocessorHelpers.isConditionalLine(lineContent))
            return false;
        return PreprocessorHelpers.isPreprocessorLine(lineContent);
    }
}

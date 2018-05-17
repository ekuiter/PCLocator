package de.ovgu.spldev.pclocator;

import java.util.HashMap;
import java.util.Stack;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class DeduceNotFoundPresenceConditionLocator extends IgnorePreprocessorPresenceConditionLocator {
    DeduceNotFoundPresenceConditionLocator(Implementation implementation, Options options) {
        super(implementation, options);
    }

    protected void deduceNotFound(HashMap<Integer, PresenceCondition> locatedPresenceConditions, String[] lineContents,
                                  IntStream lineRange, Predicate<String> isOpen, Predicate<String> isClose) {
        Stack<PresenceCondition> presenceConditionStack = new Stack<>();
        presenceConditionStack.push(PresenceCondition.TRUE);

        IntConsumer setPresenceConditionToTopOfStack = line -> {
            if (presenceConditionStack.peek() != null)
                locatedPresenceConditions.put(line, presenceConditionStack.peek());
        };

        lineRange.forEach(line -> {
            String lineContent = lineContents[line - 1];
            if (isOpen.test(lineContent)) { // matches #if, #ifdef, #ifndef on forward deducing
                setPresenceConditionToTopOfStack.accept(line); // PC of a directive is that of surrounding conditional
                presenceConditionStack.push(null); // encountered new conditional
            } else if (isClose.test(lineContent)) { // matches #endif on forward deducing
                presenceConditionStack.pop();
                setPresenceConditionToTopOfStack.accept(line);
            } else if (PreprocessorHelpers.isConditionalCloseAndOpenLine(lineContent)) { // matches #else, #elif on forward deducing
                presenceConditionStack.pop();
                setPresenceConditionToTopOfStack.accept(line);
                presenceConditionStack.push(null);
            } else { // matches any regular line of code (including #defines, #includes, ...)
                PresenceCondition presenceCondition = locatedPresenceConditions.get(line);
                // if it has a PC, replace the null placeholder so subsequently we use this (deduced) PC
                if (presenceCondition.isPresent() && presenceConditionStack.peek() == null) {
                    presenceConditionStack.pop();
                    presenceConditionStack.push(presenceCondition);
                }
                if (!presenceCondition.isPresent()) // if it doesn't have a PC, use the deduced one
                    setPresenceConditionToTopOfStack.accept(line);
            }
        });
    }

    protected HashMap<Integer, PresenceCondition> modifyPresenceConditions
            (HashMap<Integer, PresenceCondition> locatedPresenceConditions, String[] lineContents) {

        // deduce forward, replacing all not found PCs on a level after the first found PC, e.g.
        // #ifdef A
        //            // PC: not found
        // int a;     // found PC: def (A)
        //            // deduced PC: def (A)
        // #endif
        deduceNotFound(locatedPresenceConditions, lineContents,
                IntStream.rangeClosed(1, lineContents.length),
                PreprocessorHelpers::isConditionalOpenLine,
                PreprocessorHelpers::isConditionalCloseLine);

        // deduce backward, replacing all not found PCs on a level before the first found PC
        // #ifdef A
        //            // deduced PC: def (A)
        // int a;     // found PC: def (A)
        //            // (previously) deduced PC: def (A)
        // #endif
        deduceNotFound(locatedPresenceConditions, lineContents,
                IntStream.rangeClosed(1, lineContents.length).map(line -> lineContents.length - line + 1),
                PreprocessorHelpers::isConditionalCloseLine,
                PreprocessorHelpers::isConditionalOpenLine);

        return locatedPresenceConditions;
    }
}

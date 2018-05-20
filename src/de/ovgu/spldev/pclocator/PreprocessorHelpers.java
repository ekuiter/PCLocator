package de.ovgu.spldev.pclocator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreprocessorHelpers {
    // see https://gcc.gnu.org/onlinedocs/cpp/The-preprocessing-language.html
    // Preprocessor directives may span multiple lines (using '\' at the end of a line),
    // but that usually only concerns macros, which we are not interested in here.
    protected static boolean isPreprocessorLine(String lineContent) {
        return lineContent.matches("\\s*#.*");
    }

    protected static boolean isConditionalOpenLine(String lineContent) {
        return lineContent.matches("\\s*#\\s*if.*");
    }

    protected static boolean isConditionalCloseLine(String lineContent) {
        return lineContent.matches("\\s*#\\s*endif.*");
    }

    protected static boolean isConditionalCloseAndOpenLine(String lineContent) {
        return lineContent.matches("\\s*#\\s*(else|elif).*");
    }

    protected static boolean isConditionalLine(String lineContent) {
        return lineContent.matches("\\s*#\\s*(if|endif|else|elif).*");
    }

    protected static String getSystemIncludeFile(String lineContent) {
        Pattern pattern = Pattern.compile("\\s*#\\s*include\\s*<(.*)>\\s*");
        Matcher matcher = pattern.matcher(lineContent);
        return matcher.find() ? matcher.group(1) : null;
    }

    protected static String getUserIncludeFile(String lineContent) {
        Pattern pattern = Pattern.compile("\\s*#\\s*include\\s*\"(.*)\"\\s*");
        Matcher matcher = pattern.matcher(lineContent);
        return matcher.find() ? matcher.group(1) : null;
    }
}

package org.junit;

public class ComparisonCompactor {

    private static final int MAX_CONTEXT_LENGTH = 20;

    public static String getMessage(String expected, String actual) {
        return new ComparisonCompactor(MAX_CONTEXT_LENGTH, expected, actual).compact();
    }

    private static String format(Object expected, Object actual) {
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString)) {
            return "expected: "
                    + formatClassAndValue(expected, expectedString)
                    + " but was: " + formatClassAndValue(actual, actualString);
        } else {
            return "expected:<" + expectedString + "> but was:<"
                    + actualString + ">";
        }
    }

    private static String formatClassAndValue(Object value, String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

    private static final String ELLIPSIS = "...";
    private static final String DIFF_END = "]";
    private static final String DIFF_START = "[";

    /**
     * The maximum length for <code>expected</code> and <code>actual</code> strings to show. When
     * <code>contextLength</code> is exceeded, the Strings are shortened.
     */
    private final int contextLength;
    private final String expected;
    private final String actual;

    /**
     * @param contextLength the maximum length of context surrounding the difference between the compared strings.
     *                      When context length is exceeded, the prefixes and suffixes are compacted.
     * @param expected      the expected string value
     * @param actual        the actual string value
     */
    private ComparisonCompactor(int contextLength, String expected, String actual) {
        this.contextLength = contextLength;
        this.expected = expected;
        this.actual = actual;
    }

    private String compact() {
        if (expected == null || actual == null || expected.equals(actual)) {
            return format(expected, actual);
        } else {
            DiffExtractor extractor = new DiffExtractor();
            String compactedPrefix = extractor.compactPrefix();
            String compactedSuffix = extractor.compactSuffix();
            return format(
                    compactedPrefix + extractor.expectedDiff() + compactedSuffix,
                    compactedPrefix + extractor.actualDiff() + compactedSuffix);
        }
    }

    private String sharedPrefix() {
        int end = Math.min(expected.length(), actual.length());
        for (int i = 0; i < end; i++) {
            if (expected.charAt(i) != actual.charAt(i)) {
                return expected.substring(0, i);
            }
        }
        return expected.substring(0, end);
    }

    private String sharedSuffix(String prefix) {
        int suffixLength = 0;
        int maxSuffixLength = Math.min(expected.length() - prefix.length(),
                actual.length() - prefix.length()) - 1;
        for (; suffixLength <= maxSuffixLength; suffixLength++) {
            if (expected.charAt(expected.length() - 1 - suffixLength)
                    != actual.charAt(actual.length() - 1 - suffixLength)) {
                break;
            }
        }
        return expected.substring(expected.length() - suffixLength);
    }

    private class DiffExtractor {
        private final String sharedPrefix;
        private final String sharedSuffix;

        private DiffExtractor() {
            sharedPrefix = sharedPrefix();
            sharedSuffix = sharedSuffix(sharedPrefix);
        }

        String expectedDiff() {
            return extractDiff(expected);
        }

        String actualDiff() {
            return extractDiff(actual);
        }

        String compactPrefix() {
            if (sharedPrefix.length() <= contextLength) {
                return sharedPrefix;
            }
            return ELLIPSIS + sharedPrefix.substring(sharedPrefix.length() - contextLength);
        }

        String compactSuffix() {
            if (sharedSuffix.length() <= contextLength) {
                return sharedSuffix;
            }
            return sharedSuffix.substring(0, contextLength) + ELLIPSIS;
        }

        private String extractDiff(String source) {
            return DIFF_START + source.substring(sharedPrefix.length(), source.length() - sharedSuffix.length())
                    + DIFF_END;
        }
    }
}

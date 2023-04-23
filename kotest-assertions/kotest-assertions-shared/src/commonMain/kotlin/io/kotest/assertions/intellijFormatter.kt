package io.kotest.assertions

/**
 * Returns a message formatted appropriately for intellij to show a diff.
 *
 * This is the format intellij requires in order to recognize the diff:
 * https://github.com/JetBrains/intellij-community/blob/d2ef69f336b62015bbefbb2c0a9900563c94062c/java/java-runtime/src/com/intellij/rt/execution/testFrameworks/AbstractExpectedPatterns.java
 *
 * From the above link:
 * private static final Pattern ASSERT_EQUALS_PATTERN = Pattern.compile("expected:<(.*)> but was:<(.*)>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
 */
// TODO: Should this be internal?
fun intellijFormatError(expected: Expected, actual: Actual): String {
   return "expected:<${expected.value.value}> but was:<${actual.value.value}>"
}

internal fun intellijFormatErrorWithTypeInformation(
   expected: ExpectedWithType,
   actual: ActualWithType,
) = "expected:${expected.value.type}<${expected.value.value}> but was:${actual.value.type}<${actual.value.value}>"

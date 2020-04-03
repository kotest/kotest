package io.kotest.assertions

import io.kotest.fp.Option
import io.kotest.fp.getOrElse
import io.kotest.fp.orElse
import io.kotest.fp.some

object Failures {

   /**
    * Whether Kotest-related frames will be removed from the stack traces of thrown [AssertionError]s.
    *
    * This defaults to `true`. You can change it by setting the system property `kotest.failures.stacktrace.clean`
    * or at runtime, by reassigning this var.
    *
    * E.g.:
    *
    * ```
    *     -Dkotest.failures.stacktrace.clean=false
    * ```
    *
    * or
    *
    * ```
    *     Failures.shouldRemoveKotestElementsFromStacktrace = false
    * ```
    */
   var shouldRemoveKotestElementsFromStacktrace: Boolean =
      System.getProperty("kotest.failures.stacktrace.clean", "true") == "true"
}

actual fun <T : Throwable> cleanStackTrace(throwable: T): T {
   if (Failures.shouldRemoveKotestElementsFromStacktrace)
      throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
   return throwable
}

/**
 * Creates an [AssertionError] from the given message. If the platform supports nested exceptions, the cause
 * is set to the given [cause].
 */
actual fun createAssertionError(message: String, cause: Throwable?): AssertionError = AssertionError(message, cause)

/**
 * If JUnit5 is present, return an org.opentest4j.AssertionFailedError using the given message
 * and expeted and actual values, otherwise returns None.
 *
 * https://ota4j-team.github.io/opentest4j/docs/1.0.0/api/org/opentest4j/AssertionFailedError.html
 */
fun junit5AssertionFailedError(
   expected: Expected,
   actual: Actual,
   cause: Throwable?
): Option<Throwable> {
   return callPublicConstructor<Throwable>(
      "org.opentest4j.AssertionFailedError",
      arrayOf(String::class.java, Object::class.java, Object::class.java),
      arrayOf(null, expected.value.value, actual.value.value)
   )
}

/**
 * If JUnit4 is present, return a org.junit.ComparisonFailure
 * otherwise returns None.
 *
 * https://junit.org/junit4/javadoc/latest/org/junit/ComparisonFailure.html
 */
fun junit4ComparisonFailure(expected: Expected, actual: Actual): Option<Throwable> {
   return callPublicConstructor<Throwable>(
      "org.junit.ComparisonFailure",
      arrayOf(String::class.java, String::class.java, String::class.java),
      arrayOf(null, expected.value.value, actual.value.value)
   )
}

/**
 * Create an instance of the class named [className], with the [args] of type [parameterTypes]
 *
 * The constructor must be public.
 *
 * @return The constructed object, or None if any error occurred.
 */
@Suppress("UNCHECKED_CAST")
private fun <T> callPublicConstructor(
   className: String,
   parameterTypes: Array<Class<*>>,
   args: Array<Any?>
): Option<T> {
   return try {
      val targetType = Class.forName(className)
      val constructor = targetType.getConstructor(*parameterTypes)
      constructor.newInstance(*args).some() as Option<T>
   } catch (t: Throwable) {
      Option.None
   }
}

/**
 * Creates an [AssertionError] from the given message and expected and actual values. If the platform
 * supports nested exceptions, the cause is set to the given [cause]. If the platform supports stack traces,
 * then the stack is cleaned of `io.kotest` lines.
 *
 * If the platform has jUnit4 or jUnit5 on the classpath, it will use exceptions from those platforms.
 */
actual fun createAssertionError(
   message: String,
   cause: Throwable?,
   expected: Expected,
   actual: Actual
): Throwable {
   return junit5AssertionFailedError(expected, actual, cause)
      .orElse(junit4ComparisonFailure(expected, actual))
      .getOrElse(createAssertionError(message, cause))
}

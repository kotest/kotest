package io.kotest.assertions

import io.kotest.assertions.show.Printed

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

/**
 * Creates an error by falling through implementations that may or may not be on the classpath.
 * `org.opentest4j.AssertionFailedError` is preferred, then `org.junit.ComparisonFailure` or
 * finally, a `AssertionError`
 */
actual fun failure(expected: Printed, actual: Printed): Throwable {

   /** If JUnit5 is present, return an org.opentest4j.AssertionFailedError */
   fun junit5AssertionFailedError(message: String): Throwable? {
      return callPublicConstructor(
         "org.opentest4j.AssertionFailedError",
         arrayOf(String::class.java, Object::class.java, Object::class.java),
         arrayOf(message, expected.value, actual.value)
      ) as? Throwable
   }

   /** If JUnit4 is present, return a org.junit.ComparisonFailure */
   fun junit4comparisonFailure(message: String): Throwable? {
      return callPublicConstructor(
         "org.junit.ComparisonFailure",
         arrayOf(String::class.java, String::class.java, String::class.java),
         arrayOf(message, expected.value, actual.value)
      ) as? Throwable
   }

   val message = clueContextAsString() + intellijFormatError(expected, actual)

   val t = junit5AssertionFailedError(message)
      ?: junit4comparisonFailure(message)
      ?: AssertionError(message)
   cleanStackTrace(t)
   return t
}

actual fun cleanStackTrace(throwable: Throwable): Throwable {
   if (Failures.shouldRemoveKotestElementsFromStacktrace)
      throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
   return throwable
}

/**
 * Creates an error by falling through implementations that may or may not be on the classpath.
 * `org.opentest4j.AssertionFailedError` is preferred, then `org.junit.ComparisonFailure` or
 * finally, a `AssertionError`
 */
actual fun failure(message: String): Throwable {

   val withClue = clueContextAsString() + message

   /** If JUnit5 is present, return an org.opentest4j.AssertionFailedError */
   fun junit5AssertionFailedError(): Throwable? {
      return callPublicConstructor(
         "org.opentest4j.AssertionFailedError",
         arrayOf(String::class.java),
         arrayOf(withClue)
      ) as? Throwable
   }

   val t = junit5AssertionFailedError()
      ?: AssertionError(withClue)
   cleanStackTrace(t)
   return t
}

actual fun failure(message: String, cause: Throwable?): Throwable {

   /** If JUnit5 is present, return an org.opentest4j.AssertionFailedError */
   fun junit5AssertionFailedError(): Throwable? {
      return callPublicConstructor(
         "org.opentest4j.AssertionFailedError",
         arrayOf(String::class.java, Throwable::class.java),
         arrayOf(message, cause)
      ) as? Throwable
   }

   val t = junit5AssertionFailedError()
      ?: AssertionError(message, cause)
   cleanStackTrace(t)
   return t
}

/**
 * Create an instance of the class named [className], with the [args] of type [parameterTypes]
 *
 * The constructor must be public.
 *
 * @return The constructed object, or null if any error occurred.
 */
private fun callPublicConstructor(className: String, parameterTypes: Array<Class<*>>, args: Array<Any?>): Any? {
   return try {
      val targetType = Class.forName(className)
      val constructor = targetType.getConstructor(*parameterTypes)
      constructor.newInstance(*args)
   } catch (t: Throwable) {
      null
   }
}

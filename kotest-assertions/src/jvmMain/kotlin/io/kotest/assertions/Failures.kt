@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.assertions

actual object Failures {

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
    *     Failures.shouldRemoveKottestElementsFromStacktrace = false
    * ```
    */
   var shouldRemoveKotestElementsFromStacktrace: Boolean =
      System.getProperty("kotest.failures.stacktrace.clean", "true") == "true"

   actual fun failure(message: String): AssertionError = failure(message, null)
   actual fun failure(message: String, cause: Throwable?): AssertionError = AssertionError(message).apply {
      clean(this)
      initCause(cause)
   }

   /**
    * Remove Kotest-related elements from the top of [throwable]'s stack trace.
    *
    * If no Kotest-related elements are present in the stack trace, it is unchanged.
    * JVM only: If [shouldRemoveKotestElementsFromStacktrace] is `true`,
    * the stacktrace will be reduced to the user-code StackTrace only.
    */
   actual fun clean(throwable: Throwable): Throwable {
      if (shouldRemoveKotestElementsFromStacktrace)
         throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
      return throwable
   }

   actual fun failure(message: String, expectedRepr: String, actualRepr: String): Throwable {

      /**
       * Create an instance of the class named [className], with the [args] of type [parameterTypes]
       *
       * The constructor must be public.
       *
       * @return The constructed object, or null if any error occurred.
       */
      fun callPublicConstructor(className: String, parameterTypes: Array<Class<*>>, args: Array<Any?>): Any? {
         return try {
            val targetType = Class.forName(className)
            val constructor = targetType.getConstructor(*parameterTypes)
            constructor.newInstance(*args)
         } catch (t: Throwable) {
            null
         }
      }

      /** If JUnit5 is present, return an org.opentest4j.AssertionFailedError */
      fun junit5AssertionFailedError(message: String, expected: Any?, actual: Any?): Throwable? {
         return callPublicConstructor("org.opentest4j.AssertionFailedError",
            arrayOf(String::class.java, Object::class.java, Object::class.java),
            arrayOf(message, expected, actual)) as? Throwable
      }

      /** If JUnit4 is present, return a org.junit.ComparisonFailure */
      fun junit4comparisonFailure(expected: String, actual: String): Throwable? {
         return callPublicConstructor("org.junit.ComparisonFailure",
            arrayOf(String::class.java, String::class.java, String::class.java),
            arrayOf("", expected, actual)) as? Throwable
      }

      val t = junit5AssertionFailedError(message, expectedRepr, actualRepr)
         ?: junit4comparisonFailure(expectedRepr, actualRepr)
         ?: AssertionError(message)
      clean(t)
      return t
   }
}

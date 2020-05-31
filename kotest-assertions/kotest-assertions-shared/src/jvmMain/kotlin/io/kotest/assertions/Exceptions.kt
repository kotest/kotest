package io.kotest.assertions

import io.kotest.fp.Option
import io.kotest.fp.getOrElse
import io.kotest.fp.orElse
import io.kotest.fp.some

actual object Exceptions {

   /**
    * Creates an [AssertionError] from the given message. If the platform supports nested exceptions, the cause
    * is set to the given [cause].
    */
   actual fun createAssertionError(message: String, cause: Throwable?): AssertionError = AssertionError(message, cause)

   /**
    * If JUnit5 is present, return an org.opentest4j.AssertionFailedError using the given message
    * and expected and actual values, otherwise returns None.
    *
    * https://ota4j-team.github.io/opentest4j/docs/1.0.0/api/org/opentest4j/AssertionFailedError.html
    */
   private fun junit5AssertionFailedError(
      message: String,
      expected: Expected,
      actual: Actual,
      cause: Throwable?
   ): Option<Throwable> {
      return when (cause) {
         null -> callPublicConstructor<Throwable>(
            "org.opentest4j.AssertionFailedError",
            arrayOf(String::class.java, Object::class.java, Object::class.java),
            arrayOf(message, expected.value.value, actual.value.value)
         )
         else -> callPublicConstructor<Throwable>(
            "org.opentest4j.AssertionFailedError",
            arrayOf(String::class.java, Object::class.java, Object::class.java, Throwable::class.java),
            arrayOf(message, expected.value.value, actual.value.value, cause)
         )
      }
   }

   /**
    * If JUnit4 is present, return a org.junit.ComparisonFailure
    * otherwise returns None.
    *
    * https://junit.org/junit4/javadoc/latest/org/junit/ComparisonFailure.html
    */
   private fun junit4ComparisonFailure(message: String, expected: Expected, actual: Actual): Option<Throwable> {
      return callPublicConstructor<Throwable>(
         "org.junit.ComparisonFailure",
         arrayOf(String::class.java, String::class.java, String::class.java),
         arrayOf(message, expected.value.value, actual.value.value)
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
    * supports nested exceptions, the cause is set to the given [cause].
    *
    * If the platform has jUnit4 or jUnit5 on the classpath, it will use exceptions from those platforms.
    */
   actual fun createAssertionError(
      message: String,
      cause: Throwable?,
      expected: Expected,
      actual: Actual
   ): Throwable {
      return junit5AssertionFailedError(message, expected, actual, cause)
         .orElse(junit4ComparisonFailure(message, expected, actual))
         .getOrElse(createAssertionError(message, cause))
   }
}

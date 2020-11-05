package io.kotest.assertions

actual object Exceptions {

   /**
    * Creates an [AssertionError] from the given message. If the platform supports nested exceptions, the cause
    * is set to the given [cause].
    */
   actual fun createAssertionError(message: String, cause: Throwable?): AssertionError = AssertionError(message, cause)

   private val junit5AssertionFailedError = findConstructor(
      "org.opentest4j.AssertionFailedError",
      String::class.java,
      Object::class.java,
      Object::class.java
   )

   private val junit5AssertionFailedErrorWithCause = findConstructor(
      "org.opentest4j.AssertionFailedError",
      String::class.java,
      Object::class.java,
      Object::class.java,
      Throwable::class.java
   )

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
   ): Throwable? = when (cause) {
      null -> junit5AssertionFailedError?.invoke(
         arrayOf(message, expected.value.value, actual.value.value)
      )
      else -> junit5AssertionFailedErrorWithCause?.invoke(
         arrayOf(message, expected.value.value, actual.value.value, cause)
      )
   }

   private val junit4ComparisonFailureConstructor = findConstructor(
      "org.junit.ComparisonFailure",
      String::class.java,
      String::class.java,
      String::class.java
   )

   /**
    * If JUnit4 is present, return a org.junit.ComparisonFailure
    * otherwise returns None.
    *
    * https://junit.org/junit4/javadoc/latest/org/junit/ComparisonFailure.html
    */
   private fun junit4ComparisonFailure(message: String, expected: Expected, actual: Actual): Throwable? =
      junit4ComparisonFailureConstructor?.invoke(arrayOf(message, expected.value.value, actual.value.value))

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
   ): Throwable = junit5AssertionFailedError(message, expected, actual, cause)
      ?: junit4ComparisonFailure(message, expected, actual)
      ?: createAssertionError(message, cause)

   /**
    * Tries to find the public constructor for class [className] with the given [parameterTypes]. Returns
    * a function which will call the constructor with the passed argument array and return the instantiated
    * object or `null` if there is an error during invocation.
    *
    * If no constructor is found, `null` is returned.
    */
   private fun findConstructor(className: String, vararg parameterTypes: Class<*>): ((Array<Any?>) -> Throwable?)? =
      try {
         val targetType = Class.forName(className)
         val constructor = targetType.getConstructor(*parameterTypes);
         {
            try {
               constructor.newInstance(*it) as Throwable
            } catch (ignored: Throwable) {
               null
            }
         }
      } catch (ignored: Throwable) {
         null
      }
}

package io.kotest.core.spec

/**
 * Defines the execution order of specs.
 */
enum class SpecExecutionOrder {

   /**
    * The order of specs is undefined and will execute in the order they are discovered at
    * runtime (for example, from JVM classpath discovery or the order of compilation for JS or Native tests).
    *
    * This is the default execution order.
    */
   Undefined,

   /**
    * Specs are ordered lexicographically.
    */
   Lexicographic,

   /**
    * Specs are executed in a random order each time the test suite is executed.
    */
   Random,

   /**
    * Orders specs by the [Order] annotation. Any specs without such an annotation are considered "last"
    * and the order is undefined.
    */
   Annotated,

   /**
    * Orders specs such that failures on the previous run are executed first.
    * If no tests failed on the previous run, or this is the first run, then the order is undefined.
    *
    * Note: This option is only applicable for JVM tests as it requires writing state between runs.
    */
   FailureFirst
}

annotation class Order(val value: Int)

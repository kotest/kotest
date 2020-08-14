package io.kotest.core.spec

/**
 * Defines the execution order of specs.
 */
enum class SpecExecutionOrder {

   /**
    * The order of specs is undefined and will execute in the order they are discovered at
    * runtime (eg, from JVM classpath discovery, or the order they appear in a javascript file).
    *
    * This is the default execution order.
    */
   Undefined,

   /**
    * Specs are ordered lexicographically.
    */
   Lexicographic,

   /**
    * Specs are executed in a random order each time the test plan is executed.
    */
   Random,

   /**
    * Orders specs by the [@Order] annotation. Any specs without such an annotation are considered "last".
    */
   Annotated,

   /**
    * Orders specs such that failures on the previous run are executed first.
    * If no tests failed on the previous run, or this is the first run, then the order is undefined.
    *
    * This option is only applicable for JVM tests as it requires writing state between runs.
    */
   FailureFirst
}

annotation class Order(val value: Int)

package io.kotest.core.test

/**
 * This enum is used to configure the order of root test execution in a [io.kotest.core.spec.Spec].
 */
enum class TestCaseOrder {

   /**
    * Executes tests in the order they are defined in code.
    */
   Sequential,

   /**
    * A randomized ordering that will be different on each test run.
    */
   Random,

   /**
    * Execute tests alphanumerically.
    */
   Lexicographic
}

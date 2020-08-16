package io.kotest.core.test

/**
 * This enum is used to configure the order of root test execution in a [Spec].
 *
 * The default, [Sequential] executes tests in the order they are defined in code.
 * [Random] will randomize the test case ordering
 * [Lexicographic] will execute alphanumerically.
 */
enum class TestCaseOrder {

   // the order in which the tests are defined in code
   Sequential,

   // a randomized order
   Random,

   // ordered a-z
   Lexicographic
}

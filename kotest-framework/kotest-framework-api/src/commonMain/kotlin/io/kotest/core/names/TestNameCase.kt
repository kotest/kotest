package io.kotest.core.names

/**
 * Test naming strategies to adjust test name case.
 *
 * @property AsIs For: `should("Happen SOMETHING")` yields: `should Happen SOMETHING`
 * @property Sentence For: `should("Happen SOMETHING")` yields: `Should happen SOMETHING`
 * @property InitialLowercase For: `should("Happen SOMETHING")` yields: `should happen SOMETHING`
 * @property Lowercase For: `should("Happen SOMETHING")` yields: `should happen something`
 */
enum class TestNameCase {
   AsIs,
   Sentence,
   InitialLowercase,
   Lowercase
}

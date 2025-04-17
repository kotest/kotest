package io.kotest.core.test

/**
 * [AssertionMode] is used to detect and warn a developer that a test does not execute any assertions.
 *
 * It is usually the case that if a test function does not execute some kind of assertion then the test
 * is probably erroneous (see note). It is common to see junior developers write a test that does not actually test
 * anything.
 *
 * Therefore, by setting [AssertionMode] to [Error] or [Warn], the lack of assertions in a test will cause
 * the test to fail, or a warning to be outputted respectively.
 *
 * The default value of [None] is the status quo - absence of assertions will cause no issues.
 *
 * Note: There are valid use cases for not having an assertion. For example, testing that some code will
 * compile is a valid use case, and the successful return of the compiler is sufficient test.
 *
 * Warning: This mode will only work with
 * [`kotest-assertions`](https://kotest.io/docs/assertions/assertions.html).
 * If you are using another assertion library, such as
 * [`kotlin.test`](https://kotlinlang.org/api/latest/kotlin.test/) or
 * [AssertJ](https://assertj.github.io/doc/), then this setting will have no effect.
 */
enum class AssertionMode {
   Error, Warn, None
}

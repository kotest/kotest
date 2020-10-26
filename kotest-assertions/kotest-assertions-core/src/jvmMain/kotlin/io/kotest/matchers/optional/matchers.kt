package io.kotest.matchers.optional

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.util.Optional

/**
 * Asserts that a [Optional] is empty
 * 
 * ```
 * Optional.empty().shouldBeEmpty() // Assertion passes
 * 
 * Optional.of("A").shouldBeEmpty() // Assertion fails
 * ```
 */
fun <T> Optional<T>.shouldBeEmpty() = this should beEmpty()

/**
 * Asserts that a [Optional] is not empty
 * 
 * ```
 * Optional.of("A").shouldNotBeEmpty() // Assertion passes
 * 
 * Optional.empty().shouldNotBeEmpty() // Assertion fails
 * ```
 */
fun <T> Optional<T>.shouldNotBeEmpty() = this shouldNot beEmpty()

/**
 * Matcher to verify whether an [Optional] is empty or not
 */
fun <T> beEmpty() = object : Matcher<Optional<T>> {
    override fun test(value: Optional<T>) = MatcherResult(
        !value.isPresent,
        { "Expected optional to be empty, but instead was ${value.get()}" },
        { "Expected optional to not be empty, but was" }
    )
}

/**
 * Verifies if this [Optional] is present then execute [block] with it's value
 * 
 * ```
 * val optional = Optional.of("A")
 * 
 * optional shouldBePresent {
 *     it shouldBe "A"
 * }
 *
 * ```
 */
infix fun <T> Optional<T>.shouldBePresent(block: T.(value: T) -> Unit): T { 
    shouldBePresent()
    get().block(get())
    return get()
}

/**
 * Verifies if this [Optional] is present
 * 
 * ```
 * val optional = Optional.of("A")
 * 
 * optional.shouldBePresent()
 * 
 * ```
 * 
 * Further assertions can be made using the returned value
 * 
 * ```
 * val optional = Optional.of("A")
 * 
 * val present = optional.shouldBePresent()
 * present shouldBe "A"
 * ```
 * 
 */
fun <T> Optional<T>.shouldBePresent(): T {
    this should bePresent()
    return get()
}

/**
 * Verifies t hat this Optional contains no value
 */
fun <T> Optional<T>.shouldNotBePresent() = this shouldNot bePresent()

/**
 * Matcher to verify whether a matcher contains a value or not
 */
fun <T> bePresent() = object : Matcher<Optional<T>> {
    override fun test(value: Optional<T>) = MatcherResult(
        value.isPresent,
        { "Expected optional to be present, but was empty instead" },
        { "Expected optional to not be present, but was ${value.get()}" }
    )
}

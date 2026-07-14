package io.kotest.assertions.eq

import io.kotest.assertions.print.print
import kotlin.reflect.KClass

@DslMarker
annotation class EqOverridesDsl

/**
 * Type-safe builder used inside `withEqs { ... }` to register per-call [Eq] overrides.
 *
 * ```
 * actual withEqs {
 *    register<BigDecimal>(BigDecimalIgnoreScaleEq)
 *    register<Money>(MoneyEq)
 * } shouldBe expected
 * ```
 */
@EqOverridesDsl
class EqOverrides {

   internal val eqs: MutableMap<KClass<*>, Eq<*>> = mutableMapOf()

   /**
    * Registers an [Eq] instance for type [T] within the scope of the enclosing [withEqs] block.
    *
    * Calling this multiple times for the same type is allowed; the last call wins, matching the
    * builder semantics used elsewhere in Kotest (e.g. `TestConfig`).
    */
   inline fun <reified T : Any> register(eq: Eq<T>) {
      put(T::class, eq)
   }

   @PublishedApi
   internal fun put(type: KClass<*>, eq: Eq<*>) {
      eqs[type] = eq
   }
}

/**
 * Carries the actual value together with the [Eq] overrides supplied via [withEqs], waiting for a
 * matching [shouldBe] (or [shouldNotBe]) to drive the comparison.
 */
class WithEqs<T> internal constructor(
   internal val actual: T,
   internal val overrides: Map<KClass<*>, Eq<*>>,
)

/**
 * Builds [Eq] overrides scoped to a single comparison, leaving [DefaultEqResolver] untouched.
 *
 * The returned [WithEqs] composes with [shouldBe] and [shouldNotBe]:
 *
 * ```
 * mapOf("USD" to BigDecimal("3.14")) withEqs {
 *    register<BigDecimal>(BigDecimalIgnoreScaleEq)
 * } shouldBe mapOf("USD" to BigDecimal("3.140"))
 * ```
 *
 * Overrides flow through nested collection, map and data-class comparisons because the
 * [LayeredEqResolver] travels on the [EqContext] threaded through every recursive [EqCompare]
 * call.
 */
infix fun <T> T.withEqs(block: EqOverrides.() -> Unit): WithEqs<T> {
   val overrides = EqOverrides().apply(block)
   return WithEqs(this, overrides.eqs.toMap())
}

/**
 * Asserts that the value carried by [this] equals [expected] under the overrides supplied via
 * [withEqs]. Throws an [AssertionError] on mismatch and returns the actual value on success.
 */
@IgnorableReturnValue
infix fun <T> WithEqs<T>.shouldBe(expected: T?): T {
   val context = EqContext(strictNumberEq = false, resolver = LayeredEqResolver(overrides))
   @Suppress("UNCHECKED_CAST")
   val result = EqCompare.compare(actual, expected as T, context)
   if (result is EqResult.Failure) throw result.error()
   return actual
}

/**
 * Asserts that the value carried by [this] does not equal [expected] under the overrides supplied
 * via [withEqs]. Throws an [AssertionError] when the comparison reports equality and returns the
 * actual value otherwise.
 */
@IgnorableReturnValue
infix fun <T> WithEqs<T>.shouldNotBe(expected: T?): T {
   val context = EqContext(strictNumberEq = false, resolver = LayeredEqResolver(overrides))
   @Suppress("UNCHECKED_CAST")
   val result = EqCompare.compare(actual, expected as T, context)
   if (result is EqResult.Success) {
      throw AssertionError("${expected.print().value} should not equal ${actual.print().value}")
   }
   return actual
}

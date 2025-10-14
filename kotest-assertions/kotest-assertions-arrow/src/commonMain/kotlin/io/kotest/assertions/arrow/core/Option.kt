package io.kotest.assertions.arrow.core

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.kotest.assertions.arrow.shouldBe
import io.kotest.assertions.arrow.shouldNotBe
import io.kotest.matchers.assertionCounter
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * smart casts to [Some] and fails with [failureMessage] otherwise.
 * ```kotlin
 * import arrow.core.Option
 * import arrow.core.Some
 * import shouldBeSome
 *
 * fun main() {
 *   //sampleStart
 *   val list = listOf("4", "5", "6")
 *   val option = Option.fromNullable(list.getOrNull(2))
 *   val element = option.shouldBeSome()
 *   val smartCasted: Some<String> = option
 *   //sampleEnd
 *   println(smartCasted)
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class)
public fun <A> Option<A>.shouldBeSome(failureMessage: () -> String = { "Expected Some, but found None" }): A {
   contract {
      returns() implies (this@shouldBeSome is Some<A>)
   }
   assertionCounter.inc()

   return when (this) {
      None -> throw AssertionError(failureMessage())
      is Some -> value
   }
}

public infix fun <A> Option<A>.shouldBeSome(a: A): A =
   shouldBeSome().shouldBe(a)

public infix fun <A> Option<A>.shouldNotBeSome(a: A): A =
   shouldBeSome().shouldNotBe(a)

/**
 * smart casts to [None] and fails with [failureMessage] otherwise.
 * ```kotlin
 * import arrow.core.Option
 * import arrow.core.None
 * import shouldBeNone
 *
 * fun main() {
 *   //sampleStart
 *   val list = listOf("4", "5", "6")
 *   val option = Option.fromNullable(list.getOrNull(5))
 *   val element = option.shouldBeNone()
 *   val smartCasted: None = option
 *   //sampleEnd
 *   println(smartCasted)
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class)
public fun <A> Option<A>.shouldBeNone(failureMessage: (Some<A>) -> String = { "Expected None, but found Some with value ${it.value}" }): None {
   contract {
      returns() implies (this@shouldBeNone is None)
   }
   assertionCounter.inc()

   return when (this) {
      None -> None
      is Some -> throw AssertionError(failureMessage(this))
   }
}

package io.kotest.assertions.arrow.core

import arrow.core.Ior
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import arrow.core.Ior.Both
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.arrow.shouldBe
import io.kotest.assertions.arrow.shouldNotBe
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.assertionCounter
import io.kotest.matchers.should

/**
 * smart casts to [Ior.Right] and fails with [failureMessage] otherwise.
 */
@OptIn(ExperimentalContracts::class)
public fun <A, B> Ior<A, B>.shouldBeRight(failureMessage: (Ior<A, B>) -> String = { "Expected Ior.Right, but found ${it::class.simpleName}" }): B {
   contract {
      returns() implies (this@shouldBeRight is Right<B>)
   }
   assertionCounter.inc()

   return when (this) {
      is Right -> value
      else -> AssertionErrorBuilder.fail(failureMessage(this))
   }
}

public infix fun <A, B> Ior<A, B>.shouldBeRight(b: B): B =
   shouldBeRight().shouldBe(b)

public infix fun <A, B> Ior<A, B>.shouldNotBeRight(b: B): B =
   shouldBeRight().shouldNotBe(b)

/**
 * smart casts to [Ior.Left] and fails with [failureMessage] otherwise.
 */
@OptIn(ExperimentalContracts::class)
public fun <A, B> Ior<A, B>.shouldBeLeft(failureMessage: (Ior<A, B>) -> String = { "Expected Ior.Left, but found ${it::class.simpleName}" }): A {
   contract {
      returns() implies (this@shouldBeLeft is Left<A>)
   }
   assertionCounter.inc()

   return when (this) {
      is Left -> value
      else -> AssertionErrorBuilder.fail(failureMessage(this))
   }
}

public infix fun <A, B> Ior<A, B>.shouldBeLeft(a: A): A =
   shouldBeLeft().shouldBe(a)

public infix fun <A, B> Ior<A, B>.shouldNotBeLeft(a: A): A =
   shouldBeLeft().shouldNotBe(a)

public fun <A, B> beBoth(): Matcher<Ior<A, B>> = Matcher { ior ->
   MatcherResult(
      ior is Both,
      { "Expected ior to be a Both, but was: ${ior::class.simpleName}" },
      { "Expected ior to not be a Both." }
   )
}

/**
 * smart casts to [Ior.Both]
 */
@OptIn(ExperimentalContracts::class)
public fun <A, B> Ior<A, B>.shouldBeBoth(): Ior.Both<A, B> {
   contract {
      returns() implies (this@shouldBeBoth is Ior.Both<A, B>)
   }

   this should beBoth()

   return this as Ior.Both<A, B>
}

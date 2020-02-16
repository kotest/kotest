package io.kotest.assertions.arrow.eq

import arrow.typeclasses.Eq
import io.kotest.assertions.arrow.matcher
import io.kotest.matchers.Matcher
import io.kotest.matchers.should

/**
 * Provides assertions for [Eq]
 *
 * ```kotlin
 * EqAssertions(Int.eq()).run {
 *   0 shouldBeEqvTo 0
 *   0 shouldNotBeEqvTo -1
 * }
 * ```
 */
interface EqAssertions<A> {

  fun EQA(): Eq<A>

  infix fun A.shouldBeEqvTo(b: A): Unit =
    this should eqv(b)

  infix fun A.shouldNotBeEqvTo(b: A): Unit =
    this should neqv(b)

  private fun A.eqv(b: A): Matcher<A> =
    EQA().run { matcher(eqv(b), "value ${this@eqv} != expected $b") }

  private fun A.neqv(b: A): Matcher<A> =
    EQA().run { matcher(neqv(b), "value ${this@neqv} == expected not equal to $b") }

  companion object {
    operator fun <A> invoke(EQA: Eq<A>, f: (EqAssertions<A>).() -> Unit): Unit = f(object : EqAssertions<A> {
      override fun EQA(): Eq<A> = EQA
    })
  }

}

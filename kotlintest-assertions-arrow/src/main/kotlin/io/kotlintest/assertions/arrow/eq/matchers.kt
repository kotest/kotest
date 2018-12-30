package io.kotlintest.assertions.arrow.eq

import arrow.typeclasses.Eq
import io.kotlintest.Matcher
import io.kotlintest.assertions.arrow.matcher
import io.kotlintest.should
import io.kotlintest.Result as KTResult

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
    operator fun <A> invoke(EQA: Eq<A>): EqAssertions<A> = object : EqAssertions<A> {
      override fun EQA(): Eq<A> = EQA
    }
  }

}

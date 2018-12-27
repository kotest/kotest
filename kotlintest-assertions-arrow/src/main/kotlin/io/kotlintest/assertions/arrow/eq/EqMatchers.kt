package io.kotlintest.assertions.arrow.eq

import arrow.typeclasses.Eq
import arrow.typeclasses.Order
import io.kotlintest.Matcher
import io.kotlintest.Result as KTResult

interface EqMatchers<A> {

  fun EQA(): Eq<A>

  fun matcher(passed: Boolean, msg: String, negatedFailureMsg: String = msg): Matcher<A> =
    object : Matcher<A> {
      override fun test(value: A): KTResult = KTResult(passed, msg, negatedFailureMsg)
    }

  fun A.shouldBe(b: A): Matcher<A> =
    EQA().run { matcher(eqv(b), "value ${this@shouldBe} != expected $b") }

  fun A.shouldNotBe(b: A): Matcher<A> =
    EQA().run { matcher(neqv(b), "value ${this@shouldNotBe} == expected not equal to $b") }

  companion object {
    fun <A> any(): EqMatchers<A> = object : EqMatchers<A> {
      override fun EQA(): Eq<A> = Eq.any()
    }
  }

}

interface OrderMatchers<A>: EqMatchers<A> {

  fun OA(): Order<A>

  override fun EQA(): Eq<A> = OA()

  infix fun A.beGreatherThan(b: A): Matcher<A> =
    OA().run { matcher(gt(b), "value ${this@beGreatherThan} not greather than $b") }

  infix fun A.beGreatherThanOrEqual(b: A): Matcher<A> =
    OA().run { matcher(gte(b), "value ${this@beGreatherThanOrEqual} not greather or equal than $b") }

  infix fun A.beSmallerThan(b: A): Matcher<A> =
    OA().run { matcher(lt(b), "value ${this@beSmallerThan} not smaller than $b") }

  infix fun A.beSmallerThanOrEqual(b: A): Matcher<A> =
    OA().run { matcher(lte(b), "value ${this@beSmallerThanOrEqual} not smaller or equal than $b") }

}
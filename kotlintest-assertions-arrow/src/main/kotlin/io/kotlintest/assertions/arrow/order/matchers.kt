package io.kotlintest.assertions.arrow.order

import arrow.typeclasses.Eq
import arrow.typeclasses.Order
import io.kotlintest.Matcher
import io.kotlintest.assertions.arrow.eq.EqAssertions
import io.kotlintest.assertions.arrow.matcher
import io.kotlintest.should

interface OrderAssertions<A> : EqAssertions<A> {

  fun OA(): Order<A>

  override fun EQA(): Eq<A> = OA()

  infix fun A.shouldBeGreatherThan(b: A): Unit =
    this should beGreatherThan(b)

  infix fun A.shouldBeGreatherThanOrEqual(b: A): Unit =
    this should beGreatherThanOrEqual(b)

  infix fun A.shouldBeSmallerThan(b: A): Unit =
    this should beSmallerThan(b)

  infix fun A.shouldBeSmallerThanOrEqual(b: A): Unit =
    this should beSmallerThanOrEqual(b)

  private fun A.beGreatherThan(b: A): Matcher<A> =
    OA().run { matcher(gt(b), "value ${this@beGreatherThan} not greather than $b") }

  private fun A.beGreatherThanOrEqual(b: A): Matcher<A> =
    OA().run { matcher(gte(b), "value ${this@beGreatherThanOrEqual} not greather or equal than $b") }

  private fun A.beSmallerThan(b: A): Matcher<A> =
    OA().run { matcher(lt(b), "value ${this@beSmallerThan} not smaller than $b") }

  private fun A.beSmallerThanOrEqual(b: A): Matcher<A> =
    OA().run { matcher(lte(b), "value ${this@beSmallerThanOrEqual} not smaller or equal than $b") }

  companion object {
    operator fun <A> invoke(OA: Order<A>): OrderAssertions<A> = object : OrderAssertions<A> {
      override fun OA(): Order<A> = OA
    }
  }

}
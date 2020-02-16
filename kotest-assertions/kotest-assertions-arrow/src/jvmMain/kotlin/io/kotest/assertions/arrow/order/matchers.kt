package io.kotest.assertions.arrow.order

import arrow.typeclasses.Eq
import arrow.typeclasses.Order
import io.kotest.assertions.arrow.eq.EqAssertions
import io.kotest.assertions.arrow.matcher
import io.kotest.matchers.Matcher
import io.kotest.matchers.should

/**
 * Provides assertions for [Order]
 *
 * ```kotlin
 * Int.order().assert {
 *   0 shouldBeEqvTo 0
 *   0 shouldNotBeEqvTo -1
 *   0 shouldBeGreaterThan -1
 *   0 shouldBeGreaterThanOrEqual 0
 *   0 shouldBeSmallerThan 1
 *   0 shouldBeSmallerThanOrEqual 0
 * }
 * ```
 */
interface OrderAssertions<A> : EqAssertions<A> {

  fun OA(): Order<A>

  override fun EQA(): Eq<A> = OA()

  infix fun A.shouldBeGreaterThan(b: A): Unit =
    this should beGreaterThan(b)

  infix fun A.shouldBeGreaterThanOrEqual(b: A): Unit =
    this should beGreaterThanOrEqual(b)

  infix fun A.shouldBeSmallerThan(b: A): Unit =
    this should beSmallerThan(b)

  infix fun A.shouldBeSmallerThanOrEqual(b: A): Unit =
    this should beSmallerThanOrEqual(b)

  fun A.beGreaterThan(b: A): Matcher<A> =
    OA().run { matcher(gt(b), "value ${this@beGreaterThan} not greater than $b") }

  fun A.beGreaterThanOrEqual(b: A): Matcher<A> =
    OA().run { matcher(gte(b), "value ${this@beGreaterThanOrEqual} not greater or equal than $b") }

  fun A.beSmallerThan(b: A): Matcher<A> =
    OA().run { matcher(lt(b), "value ${this@beSmallerThan} not smaller than $b") }

  fun A.beSmallerThanOrEqual(b: A): Matcher<A> =
    OA().run { matcher(lte(b), "value ${this@beSmallerThanOrEqual} not smaller or equal than $b") }

  companion object {
    operator fun <A> invoke(OA: Order<A>, f: (OrderAssertions<A>).() -> Unit): Unit = f(object : OrderAssertions<A> {
      override fun OA(): Order<A> = OA
    })
  }
}

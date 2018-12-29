package io.kotlintest.assertions.arrow.eq

import arrow.Kind
import arrow.effects.*
import arrow.effects.deferredk.applicative.applicative
import arrow.effects.instances.io.applicative.applicative
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Order
import arrow.validation.Refinement
import io.kotlintest.Matcher
import io.kotlintest.properties.Gen
import io.kotlintest.properties.PropertyContext
import io.kotlintest.properties.forAll
import io.kotlintest.should
import io.kotlintest.Result as KTResult

private fun <A> matcher(
  passed: Boolean,
  msg: String,
  negatedFailureMsg: String = msg
): Matcher<A> =
  object : Matcher<A> {
    override fun test(value: A): KTResult = KTResult(passed, msg, negatedFailureMsg)
  }

interface EqMatchers<A> {

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
    fun <A> any(): EqMatchers<A> = object : EqMatchers<A> {
      override fun EQA(): Eq<A> = Eq.any()
    }
  }

}

interface OrderMatchers<A>: EqMatchers<A> {

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

}

interface EffectMatchers<F> {

  fun AF(): Applicative<F>

  fun <A> Kind<F, A>.be(a: A): Kind<F, Matcher<A>> =
    AF().run {
      map { x -> matcher<A>(x == a, "$x is not equal to $a") }
    }

  infix fun <A> Kind<F, A>.shouldBeInterpretedTo(a: A): Unit =
    AF().run {
      map(this@shouldBeInterpretedTo, be(a)) { (a, matcher) ->
        a should matcher
      }.blockingValue()
    }

  fun <A> Kind<F, A>.blockingValue(): A

}

@extension
interface IOEffectMatchers : EffectMatchers<ForIO> {
  override fun AF(): Applicative<ForIO> = IO.applicative()

  override fun <A> Kind<ForIO, A>.blockingValue(): A = fix().unsafeRunSync()
}

@extension
interface DefferedEffectMatchers : EffectMatchers<ForDeferredK> {
  override fun AF(): Applicative<ForDeferredK> = DeferredK.applicative()

  override fun <A> Kind<ForDeferredK, A>.blockingValue(): A = fix().unsafeRunSync()
}

infix fun <F, A> A.shouldBeRefinedBy(refinement: Refinement<F, A>): Unit =
  this should beRefinedBy(refinement)

fun <F, A> A.beRefinedBy(refinement: Refinement<F, A>): Matcher<A> =
  refinement.run {
    matcher(
      passed = this@beRefinedBy.refinement(),
      msg = invalidValueMsg(this@beRefinedBy)
    )
  }

fun <F, A> forAll(GA: Gen<A>, refinement: Refinement<F, A>, f: (A) -> Boolean): Unit =
  refinement.applicativeError().run {
    val genA: Gen<A> = GA.filter { it.beRefinedBy(refinement).test(it).passed }
    val property: PropertyContext.(A) -> Boolean = { f(it) }
    forAll(genA, property)
  }

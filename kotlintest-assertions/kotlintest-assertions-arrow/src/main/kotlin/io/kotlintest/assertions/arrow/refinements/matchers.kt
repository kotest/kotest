package io.kotlintest.assertions.arrow.refinements

import arrow.validation.Refinement
import io.kotlintest.Matcher
import io.kotlintest.assertions.arrow.matcher
import io.kotlintest.properties.Gen
import io.kotlintest.properties.PropertyContext
import io.kotlintest.should

/**
 * Asserts that a value of type [A] complies with the [refinement]
 */
infix fun <F, A> A.shouldBeRefinedBy(refinement: Refinement<F, A>): Unit =
  this should beRefinedBy(refinement)

/**
 * Creates a [Matcher] for [A] given the [refinement] rules
 */
fun <F, A> A.beRefinedBy(refinement: Refinement<F, A>): Matcher<A> =
  refinement.run {
    matcher(
      passed = this@beRefinedBy.refinement(),
      msg = invalidValueMsg(this@beRefinedBy)
    )
  }

/**
 * Filters [GA] creating a new generator that complies with the rules in [refinement]
 */
fun <F, A> forAll(GA: Gen<A>, refinement: Refinement<F, A>, f: (A) -> Boolean): Unit =
  refinement.applicativeError().run {
    val genA: Gen<A> = GA.filter { it.beRefinedBy(refinement).test(it).passed() }
    val property: PropertyContext.(A) -> Boolean = { f(it) }
    io.kotlintest.properties.forAll(genA, property)
  }

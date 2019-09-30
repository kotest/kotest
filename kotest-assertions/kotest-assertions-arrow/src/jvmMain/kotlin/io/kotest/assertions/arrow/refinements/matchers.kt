package io.kotest.assertions.arrow.refinements

import arrow.validation.Refinement
import io.kotest.Matcher
import io.kotest.assertions.arrow.matcher
import io.kotest.properties.Gen
import io.kotest.properties.PropertyContext
import io.kotest.should

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
    io.kotest.properties.forAll(genA, property)
  }

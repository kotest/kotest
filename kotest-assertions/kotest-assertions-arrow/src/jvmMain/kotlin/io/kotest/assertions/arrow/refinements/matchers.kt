package io.kotest.assertions.arrow.refinements

import arrow.validation.Refinement
import io.kotest.assertions.arrow.matcher
import io.kotest.matchers.Matcher
import io.kotest.matchers.should

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

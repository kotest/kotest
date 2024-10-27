package io.kotest.property.core.checks

import io.kotest.property.core.IterationResult
import io.kotest.property.core.PermutationContext

/**
 * Logic that is invoked after each iteration of a property test.
 *
 * An [IterationCheck] can throw an error if the property test should be stopped.
 */
internal interface IterationCheck {
   fun evaluate(context: PermutationContext, result: IterationResult)
}

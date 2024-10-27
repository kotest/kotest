package io.kotest.property.core.checks

import io.kotest.property.core.PermutationContext
import io.kotest.property.core.PermutationResult

/**
 * Logic that is invoked before the property test starts.
 */
internal interface BeforeCheck {
   fun evaluate(context: PermutationContext)
}

/**
 * Logic that is invoked after the property test completes.
 */
internal interface AfterCheck {
   suspend fun evaluate(context: PermutationContext, result: PermutationResult)
}

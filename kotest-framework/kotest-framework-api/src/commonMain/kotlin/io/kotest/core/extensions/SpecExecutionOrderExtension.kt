package io.kotest.core.extensions

import io.kotest.core.spec.SpecRef

/**
 * An extension point that is used to sort specs before execution.
 *
 * If multiple instances of this extension are defined then all will be
 * invoked but the order of invocation is undefined.
 */
interface SpecExecutionOrderExtension {
   fun sort(specs: List<SpecRef>): List<SpecRef>
}

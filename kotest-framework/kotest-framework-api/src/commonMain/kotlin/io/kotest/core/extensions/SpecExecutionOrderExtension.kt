package io.kotest.core.extensions

import io.kotest.core.spec.SpecRef

/**
 * An extension point that is used to sort specs before execution.
 *
 * If multiple instances of this extension are defined then all will be
 * invoked but the order of invocation is undefined.
 *
 * If no [SpecExecutionOrderExtension]s are registered, then specs will be
 * sorted using the [io.kotest.engine.spec.DefaultSpecExecutionOrderExtension] which uses the value of
 * [io.kotest.core.config.AbstractProjectConfig.specExecutionOrder] defined in configuration.
 */
interface SpecExecutionOrderExtension {
   fun sort(specs: List<SpecRef>): List<SpecRef>
}

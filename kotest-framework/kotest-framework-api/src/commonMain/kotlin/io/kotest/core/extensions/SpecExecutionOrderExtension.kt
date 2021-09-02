package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * An extension point that is used to sort specs before execution.
 *
 * If multiple instances of this extension are defined then all will be
 * invoked but with the order of invocation undefined.
 */
interface SpecExecutionOrderExtension {
   fun sortSpecs(specs: List<Spec>): List<Spec>
   fun sortClasses(classes: List<KClass<out Spec>>): List<KClass<out Spec>>
}

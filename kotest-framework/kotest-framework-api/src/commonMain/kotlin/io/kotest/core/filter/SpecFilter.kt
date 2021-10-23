package io.kotest.core.filter

import io.kotest.core.extensions.Extension
import kotlin.reflect.KClass

/**
 * A [SpecFilter] can be used to filter [Spec] classes before they are instantiated.
 * These filters are passed to the Kotest Engine at runtime.
 */
interface SpecFilter : Extension {

   /**
    * This method is invoked with a spec [KClass] and the result
    * used to determine if the test should be included or not.
    */
   fun filter(kclass: KClass<*>): SpecFilterResult
}

sealed interface SpecFilterResult {

   /**
    * Include the spec at runtime.
    */
   object Include : SpecFilterResult

   /**
    * Exclude the spec at runtime. It will not appear in the output at all.
    */
   object Exclude : SpecFilterResult

   /**
    * Mark the spec as skipped with an optional reason.
    */
   data class Ignore(val reason: String?) : SpecFilterResult
}

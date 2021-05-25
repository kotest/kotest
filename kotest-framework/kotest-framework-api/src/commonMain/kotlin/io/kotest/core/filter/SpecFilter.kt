package io.kotest.core.filter

import kotlin.reflect.KClass

/**
 * A [SpecFilter] can be used to filter [Spec] classes before they are instantiated.
 * These filters are passed to the Kotest Engine at runtime.
 */
interface SpecFilter {

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
    * Exclude the spec but include it as an "ignored" test, with an optional reason.
    */
   data class Ignore(val reason: String?) : SpecFilterResult
}

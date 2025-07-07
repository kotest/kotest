package io.kotest.engine.extensions

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.extensions.Extension

/**
 * A [DescriptorFilter] can be used to filter specs and tests before they are executed.
 *
 * A given [Descriptor] must be included by all filters for it to be considered enabled at runtime,
 * or in other words, if any filter returns [DescriptorFilterResult.Exclude], then the spec or test
 * will not be executed.
 *
 * If no filters are registered, then all specs and tests will be executed.
 */
interface DescriptorFilter : Extension {

   /**
    * This method is invoked with a [Descriptor] and the result
    * used to determine if the descriptor should be included or not.
    */
   fun filter(descriptor: Descriptor): DescriptorFilterResult
}

sealed interface DescriptorFilterResult {

   /**
    * Include the spec at runtime.
    */
   data object Include : DescriptorFilterResult

   /**
    * Exclude the spec at runtime with an optional reason.
    */
   data class Exclude(val reason: String?) : DescriptorFilterResult
}


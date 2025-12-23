package io.kotest.engine.extensions

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.extensions.Extension

/**
 * A [DescriptorFilter] is an [Extension] that is used to filter specs and tests before they are executed.
 *
 * A given [Descriptor] must be included by ALL filters for it to be considered enabled at runtime,
 * or in other words, if any filter returns [DescriptorFilterResult.Exclude], then matching specs and tests
 * will not be executed, even if other filters return [DescriptorFilterResult.Include].
 *
 * If no filters are registered, then all specs and tests will be executed.
 *
 * See [IncludeTestPatternDescriptorFilter].
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


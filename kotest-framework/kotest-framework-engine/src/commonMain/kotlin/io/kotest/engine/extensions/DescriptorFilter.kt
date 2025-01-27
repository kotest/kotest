package io.kotest.engine.extensions

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.extensions.Extension

/**
 * A [DescriptorFilter] can be used to filter specs and tests before they are executed.
 * A given [Descriptor] must be included by all filters for it to be considered enabled at runtime.
 * If no filters are registered, then no specs and tests will be excluded.
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

/**
 * An implementation of [DescriptorFilter] that only includes descriptors that are
 * members, or descendents of, the given [accept] list.
 */
class ProvidedDescriptorFilter(private vararg val accept: Descriptor) : DescriptorFilter {
   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      val include = accept.any { it.hasSharedPath(descriptor) }
      println("ProvidedDescriptorFilter: $descriptor included = $include $accept = ${accept.toList()}")
      return if (include) DescriptorFilterResult.Include else DescriptorFilterResult.Exclude(null)
   }
}

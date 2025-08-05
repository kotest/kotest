package io.kotest.engine.extensions

import io.kotest.core.descriptors.Descriptor

/**
 * An implementation of [DescriptorFilter] that only includes any descriptors that are
 * included in or descendents of, the given [accept] list.
 */
class IncludeDescriptorFilter(private vararg val accept: Descriptor) : DescriptorFilter {

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      val include = accept.any { it.hasSharedPath(descriptor) }
      return if (include) DescriptorFilterResult.Include else DescriptorFilterResult.Exclude(null)
   }

}

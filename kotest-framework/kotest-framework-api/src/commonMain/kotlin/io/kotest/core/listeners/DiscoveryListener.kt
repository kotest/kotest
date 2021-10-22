package io.kotest.core.listeners

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId

interface DiscoveryListener {

   /**
    * Is invoked after all the [Spec] classes have been discovered.
    * This callback will be called after any [DiscoveryExtension]s have been invoked.
    *
    * In other words the descriptors given here are after discovery extensions
    * have had the chance to filter.
    *
    * @param descriptors the [DescriptorId] instance for each Spec class discovered.
    */
   fun afterDiscovery(descriptors: List<Descriptor.SpecDescriptor>): Unit = Unit
}

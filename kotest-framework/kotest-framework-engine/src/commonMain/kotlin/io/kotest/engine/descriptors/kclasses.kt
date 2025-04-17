package io.kotest.engine.descriptors

import io.kotest.core.descriptors.Descriptor.SpecDescriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Returns a [SpecDescriptor] for a class using the fully qualified name for the identifier.
 *
 * On platforms where the FQN is not available, this will fall back to the simple class name.
 */
fun KClass<*>.toDescriptor(): SpecDescriptor {
   return SpecDescriptor(DescriptorId(this.bestName()))
}

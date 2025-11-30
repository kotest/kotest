package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor

/**
 * Returns a new [UniqueId] by appending this [descriptor] to the receiver.
 */
internal fun UniqueId.append(descriptor: Descriptor): UniqueId {
   val segment = when (descriptor) {
      is Descriptor.SpecDescriptor -> Segment.Spec
      is Descriptor.TestDescriptor -> Segment.Test
   }
   return this.append(segment.value, descriptor.id.value)
}

internal sealed class Segment {
   abstract val value: String

   object Spec : Segment() {
      override val value: String = "spec"
   }

   object Test : Segment() {
      override val value: String = "test"
   }
}

/**
 * The created [UniqueId] will have segment type [Segment.Spec] and will use the descriptor id.
 */
internal fun EngineDescriptor.deriveSpecUniqueId(id: DescriptorId): UniqueId =
   uniqueId.append(Segment.Spec.value, id.value)

/**
 * The created [UniqueId] will have segment type [Segment.Test] and will use the descriptor id.
 */
internal fun EngineDescriptor.deriveTestUniqueId(descriptor: Descriptor): UniqueId {
   return when (descriptor) {
      is Descriptor.SpecDescriptor -> deriveSpecUniqueId(descriptor.id)
      is Descriptor.TestDescriptor -> deriveTestUniqueId(descriptor.parent)
         .append(Segment.Test.value, descriptor.id.value)
   }
}

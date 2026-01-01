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
 * Creates a [UniqueId] for a spec from the given [EngineDescriptor] and [DescriptorId].
 *
 * The created id will have segment type [Segment.Spec].
 */
internal fun createUniqueIdForSpec(engineId: UniqueId, id: DescriptorId): UniqueId =
   engineId.append(Segment.Spec.value, id.value)

/**
 * Creates a [UniqueId] for a test from the given [EngineDescriptor] and [Descriptor.TestDescriptor].
 *
 * The created id will have segment type [Segment.Test], and any parent tests plus the spec will
 * be prepended to the created id.
 */
internal fun createTestUniqueId(engineId: UniqueId, descriptor: Descriptor.TestDescriptor): UniqueId {
   val parentDescriptor = when (val parent = descriptor.parent) {
      is Descriptor.SpecDescriptor -> createUniqueIdForSpec(engineId, parent.id)
      is Descriptor.TestDescriptor -> createTestUniqueId(engineId, parent)
   }
   return parentDescriptor.append(Segment.Test.value, descriptor.id.value)
}

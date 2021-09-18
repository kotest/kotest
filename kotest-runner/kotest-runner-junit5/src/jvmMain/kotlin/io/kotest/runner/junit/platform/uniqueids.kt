package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.Descriptor
import org.junit.platform.engine.UniqueId

/**
 * Returns a new [UniqueId] by appending this [descriptor] to the receiver.
 */
fun UniqueId.append(descriptor: Descriptor): UniqueId {
   val segment = when (descriptor) {
      is Descriptor.SpecDescriptor -> Segment.Spec
      is Descriptor.TestDescriptor -> Segment.Test
   }
   return this.append(segment.value, descriptor.id.value)
}

sealed class Segment {
   abstract val value: String

   object Spec : Segment() {
      override val value: String = "spec"
   }

   object Script : Segment() {
      override val value: String = "script"
   }

   object Test : Segment() {
      override val value: String = "test"
   }
}

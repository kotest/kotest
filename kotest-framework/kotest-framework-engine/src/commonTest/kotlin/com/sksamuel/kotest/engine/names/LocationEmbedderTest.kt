package com.sksamuel.kotest.engine.names

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.names.LocationEmbedder
import io.kotest.matchers.shouldBe

class LocationEmbedderTest : FunSpec() {
   init {
      test("happy path") {
         val descriptor = Descriptor.TestDescriptor(
            parent = Descriptor.TestDescriptor(
               parent = Descriptor.TestDescriptor(
                  parent = Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1")),
                  id = DescriptorId("rooty mcrootface")
               ),
               id = DescriptorId("context")
            ),
            id = DescriptorId("my test")
         )
         LocationEmbedder.embeddedTestName(descriptor, "display name") shouldBe
            "<kotest>com.sksamuel.Spec1/rooty mcrootface -- context -- my test</kotest>display name"
      }
   }
}

package com.sksamuel.kotest.engine.descriptors

import io.kotest.common.DescriptorPath
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.descriptors.DescriptorPaths
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DescriptorPathsTest : FunSpec() {
   init {

      test("parse with spec only") {
         DescriptorPaths.parse("com.sksamuel.Spec1") shouldBe
            Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1"))
      }

      test("parse with spec only but delimiter") {
         DescriptorPaths.parse("com.sksamuel.Spec1/") shouldBe
            Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1"))
      }
      test("parse with spec only but delimiter and whitespace") {
         DescriptorPaths.parse("com.sksamuel.Spec1/         ") shouldBe
            Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1"))
      }

      test("parse with spec and root test") {
         DescriptorPaths.parse("com.sksamuel.Spec1/rooty mcrootface") shouldBe
            Descriptor.TestDescriptor(
               parent = Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1")),
               id = DescriptorId("rooty mcrootface")
            )
      }

      test("parse with spec and nested tests") {
         DescriptorPaths.parse("com.sksamuel.Spec1/rooty mcrootface -- context -- my test") shouldBe
            Descriptor.TestDescriptor(
               parent = Descriptor.TestDescriptor(
                  parent = Descriptor.TestDescriptor(
                     parent = Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1")),
                     id = DescriptorId("rooty mcrootface")
                  ),
                  id = DescriptorId("context")
               ),
               id = DescriptorId("my test")
            )
      }

      test("filter should trim whitespace from input") {
         DescriptorPaths.parse("         com.sksamuel.Spec1   /    rooty mcrootface          ") shouldBe
            Descriptor.TestDescriptor(
               parent = Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1")),
               id = DescriptorId("rooty mcrootface")
            )
      }

      test("render with spec only") {
         DescriptorPaths.render(Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1"))) shouldBe
            DescriptorPath("com.sksamuel.Spec1")
      }

      test("render with test") {
         DescriptorPaths.render(
            Descriptor.TestDescriptor(
               parent = Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1")),
               id = DescriptorId("rooty mcrootface")
            )
         ) shouldBe DescriptorPath("com.sksamuel.Spec1/rooty mcrootface")
      }

      test("parse should remove line breaks") {
         DescriptorPaths.parse(
            """         com.sksamuel.Spec1   /    rooty
            mcrootface   """
         ) shouldBe
            Descriptor.TestDescriptor(
               parent = Descriptor.SpecDescriptor(DescriptorId("com.sksamuel.Spec1")),
               id = DescriptorId("rooty mcrootface")
            )
      }
   }
}

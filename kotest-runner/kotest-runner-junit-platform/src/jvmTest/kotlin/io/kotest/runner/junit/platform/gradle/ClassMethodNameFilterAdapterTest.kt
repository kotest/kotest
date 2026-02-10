package io.kotest.runner.junit.platform.gradle

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.matchers.shouldBe

/**
 * Tests that multiple --tests filters are correctly combined with OR logic.
 *
 * When Gradle receives multiple --tests arguments (e.g., --tests TestA --tests TestB),
 * each becomes a separate pattern. All patterns must be combined with OR logic so that
 * a test is included if it matches ANY pattern.
 *
 * Regression test for https://github.com/kotest/kotest/issues/5678
 */
class ClassMethodNameFilterAdapterTest : FunSpec({

   context("multiple regex patterns should use OR logic") {

      test("spec matching any of the patterns should be INCLUDED") {
         val specA = Descriptor.SpecDescriptor(DescriptorId("com.example.TestA"))
         val specB = Descriptor.SpecDescriptor(DescriptorId("com.example.TestB"))

         // Simulate two --tests patterns combined into a single filter
         val filter = GradleClassMethodRegexTestFilter(
            setOf("\\Qcom.example.TestA\\E", "\\Qcom.example.TestB\\E")
         )

         filter.filter(specA) shouldBe DescriptorFilterResult.Include
         filter.filter(specB) shouldBe DescriptorFilterResult.Include
      }

      test("spec matching none of the patterns should be EXCLUDED") {
         val specC = Descriptor.SpecDescriptor(DescriptorId("com.example.TestC"))

         val filter = GradleClassMethodRegexTestFilter(
            setOf("\\Qcom.example.TestA\\E", "\\Qcom.example.TestB\\E")
         )

         filter.filter(specC) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }

   context("CombinedGradleDescriptorFilter should use OR logic across both filter types") {

      test("spec matching regex pattern should be INCLUDED even when nested filter does not match") {
         val spec = Descriptor.SpecDescriptor(DescriptorId("com.example.TestA"))
         val nestedArg = NestedTestArg("com.example", "TestB", listOf("context", "nested"))

         val filter = CombinedGradleDescriptorFilter(
            regexPatterns = setOf("\\Qcom.example.TestA\\E"),
            nestedArgs = setOf(nestedArg),
         )

         filter.filter(spec) shouldBe DescriptorFilterResult.Include
      }

      test("nested test matching nested arg should be INCLUDED even when regex filter does not match") {
         val spec = Descriptor.SpecDescriptor(DescriptorId("com.example.TestB"))
         val context = spec.append("context")
         val nested = context.append("nested")
         val nestedArg = NestedTestArg("com.example", "TestB", listOf("context", "nested"))

         val filter = CombinedGradleDescriptorFilter(
            regexPatterns = setOf("\\Qcom.example.TestA\\E"),
            nestedArgs = setOf(nestedArg),
         )

         filter.filter(nested) shouldBe DescriptorFilterResult.Include
      }

      test("descriptor matching neither type should be EXCLUDED") {
         val spec = Descriptor.SpecDescriptor(DescriptorId("com.example.TestC"))
         val nestedArg = NestedTestArg("com.example", "TestB", listOf("context", "nested"))

         val filter = CombinedGradleDescriptorFilter(
            regexPatterns = setOf("\\Qcom.example.TestA\\E"),
            nestedArgs = setOf(nestedArg),
         )

         filter.filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }
})

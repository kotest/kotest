package io.kotest.runner.junit.platform.gradle

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class NestedTestsArgDescriptorFilterTest : FunSpec({

   test("include packages") {
      val spec = NestedTestsArgDescriptorFilterTest::class.toDescriptor()
      val args1 = NestedTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.NestedGradleTestsArgDescriptorFilterTest.a__--__b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(spec) shouldBe DescriptorFilterResult.Include

      val args2 = NestedTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.xxxxxx.NestedGradleTestsArgDescriptorFilterTest.a__--__b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args2)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include classes") {
      val spec = NestedTestsArgDescriptorFilterTest::class.toDescriptor()
      val args1 = NestedTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.NestedGradleTestsArgDescriptorFilterTest.a__--__b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(spec) shouldBe DescriptorFilterResult.Include

      val args2 = NestedTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.xxxxxx.Y.a__--__b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args2)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include matching nested tests") {
      val spec = NestedTestsArgDescriptorFilterTest::class.toDescriptor()
      val test1 = spec.append("a")
      val test2 = test1.append("b")
      val test3 = spec.append("c")
      val test4 = test1.append("d")
      val args1 = NestedTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.NestedGradleTestsArgDescriptorFilterTest.a__--__b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(test1) shouldBe DescriptorFilterResult.Include
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(test2) shouldBe DescriptorFilterResult.Include
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(test3) shouldBe DescriptorFilterResult.Exclude(null)
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(test4) shouldBe DescriptorFilterResult.Exclude(null)
   }
})

package com.sksamuel.kotest.engine.gradle

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.engine.gradle.NestedGradleTestsArgDescriptorFilter
import io.kotest.engine.gradle.NestedGradleTestsArgParser
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class NestedGradleTestsArgDescriptorFilterTest : FunSpec({

   test("include packages") {
      val spec = NestedGradleTestsArgDescriptorFilterTest::class.toDescriptor()
      val args1 = NestedGradleTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle\\E")!!
      NestedGradleTestsArgDescriptorFilter(setOf(args1)).filter(spec) shouldBe DescriptorFilterResult.Include

      val args2 = NestedGradleTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.xxxxx\\E")!!
      NestedGradleTestsArgDescriptorFilter(setOf(args2)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include classes") {
      val spec = NestedGradleTestsArgDescriptorFilterTest::class.toDescriptor()
      val args1 =
         NestedGradleTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.IntellijGradleTestsArgDescriptorFilterTest\\E")!!
      NestedGradleTestsArgDescriptorFilter(setOf(args1)).filter(spec) shouldBe DescriptorFilterResult.Include

      val args2 =
         NestedGradleTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.XxxGradleTestsArgDescriptorFilterTest\\E")!!
      NestedGradleTestsArgDescriptorFilter(setOf(args2)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("includes with test paths") {
      val spec = NestedGradleTestsArgDescriptorFilterTest::class.toDescriptor()
      val test1 = spec.append("foo")
      val args1 =
         NestedGradleTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.IntellijGradleTestsArgDescriptorFilterTest.foo\\E")!!
      NestedGradleTestsArgDescriptorFilter(setOf(args1)).filter(test1) shouldBe DescriptorFilterResult.Include

      val test2 = test1.append("bar")
      val args2 =
         NestedGradleTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.IntellijGradleTestsArgDescriptorFilterTest.foo__context__bar\\E")!!
      NestedGradleTestsArgDescriptorFilter(setOf(args2)).filter(test2) shouldBe DescriptorFilterResult.Include

      val args3 =
         NestedGradleTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.IntellijGradleTestsArgDescriptorFilterTest.bar\\E")!!
      NestedGradleTestsArgDescriptorFilter(setOf(args3)).filter(test1) shouldBe DescriptorFilterResult.Exclude(null)
   }
})

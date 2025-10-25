package com.sksamuel.kotest.engine.gradle

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.engine.gradle.IntellijGradleTestsArgDescriptorFilter
import io.kotest.engine.gradle.TestArgParser
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class IntellijGradleTestsArgDescriptorFilterTest : FunSpec({

   test("include packages") {
      val spec = IntellijGradleTestsArgDescriptorFilterTest::class.toDescriptor()
      val args1 = TestArgParser.parse("\\Qkotest_intellij_plugin.com.sksamuel.kotest.engine.gradle\\E")!!
      IntellijGradleTestsArgDescriptorFilter(setOf(args1)).filter(spec) shouldBe DescriptorFilterResult.Include

      val args2 = TestArgParser.parse("\\Qkotest_intellij_plugin.com.sksamuel.kotest.engine.xxxxx\\E")!!
      IntellijGradleTestsArgDescriptorFilter(setOf(args2)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include classes") {
      val spec = IntellijGradleTestsArgDescriptorFilterTest::class.toDescriptor()
      val args1 =
         TestArgParser.parse("\\Qkotest_intellij_plugin.com.sksamuel.kotest.engine.gradle.IntellijGradleTestsArgDescriptorFilterTest\\E")!!
      IntellijGradleTestsArgDescriptorFilter(setOf(args1)).filter(spec) shouldBe DescriptorFilterResult.Include

      val args2 =
         TestArgParser.parse("\\Qkotest_intellij_plugin.com.sksamuel.kotest.engine.gradle.XxxGradleTestsArgDescriptorFilterTest\\E")!!
      IntellijGradleTestsArgDescriptorFilter(setOf(args2)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("includes with test paths") {
      val spec = IntellijGradleTestsArgDescriptorFilterTest::class.toDescriptor()
      val test1 = spec.append("foo")
      val args1 =
         TestArgParser.parse("\\Qkotest_intellij_plugin.com.sksamuel.kotest.engine.gradle.IntellijGradleTestsArgDescriptorFilterTest.foo\\E")!!
      IntellijGradleTestsArgDescriptorFilter(setOf(args1)).filter(test1) shouldBe DescriptorFilterResult.Include

      val test2 = test1.append("bar")
      val args2 =
         TestArgParser.parse("\\Qkotest_intellij_plugin.com.sksamuel.kotest.engine.gradle.IntellijGradleTestsArgDescriptorFilterTest.foo__context__bar\\E")!!
      IntellijGradleTestsArgDescriptorFilter(setOf(args2)).filter(test2) shouldBe DescriptorFilterResult.Include

      val args3 =
         TestArgParser.parse("\\Qkotest_intellij_plugin.com.sksamuel.kotest.engine.gradle.IntellijGradleTestsArgDescriptorFilterTest.bar\\E")!!
      IntellijGradleTestsArgDescriptorFilter(setOf(args3)).filter(test1) shouldBe DescriptorFilterResult.Exclude(null)
   }
})

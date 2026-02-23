package io.kotest.runner.junit.platform.gradle

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class NestedTestsArgDescriptorFilterTest : FunSpec({

   test("include matching packages") {
      val spec = NestedTestsArgDescriptorFilterTest::class.toDescriptor()
      val args1 = NestedTestsArgParser.parse("\\Qio.kotest.runner.junit.platform.gradle.NestedTestsArgDescriptorFilterTest.a -- b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(spec) shouldBe DescriptorFilterResult.Include

      val args2 = NestedTestsArgParser.parse("\\Qio.kotest.runner.junit.platform.xxx.NestedTestsArgDescriptorFilterTest.a -- b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args2)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include matching classes") {
      val spec = NestedTestsArgDescriptorFilterTest::class.toDescriptor()
      val args1 = NestedTestsArgParser.parse("\\Qio.kotest.runner.junit.platform.gradle.NestedTestsArgDescriptorFilterTest.a -- b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(spec) shouldBe DescriptorFilterResult.Include

      val args2 = NestedTestsArgParser.parse("\\Qio.kotest.runner.junit.platform.gradle.NotThisClass.a -- b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args2)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include matching nested tests") {
      val spec = NestedTestsArgDescriptorFilterTest::class.toDescriptor()
      val test1 = spec.append("a")
      val test2 = test1.append("b")
      val test3 = spec.append("c")
      val test4 = test1.append("d")
      val args1 = NestedTestsArgParser.parse("\\Qio.kotest.runner.junit.platform.gradle.NestedTestsArgDescriptorFilterTest.a -- b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(test1) shouldBe DescriptorFilterResult.Include
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(test2) shouldBe DescriptorFilterResult.Include
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(test3) shouldBe DescriptorFilterResult.Exclude(null)
      NestedTestsArgDescriptorFilter(setOf(args1)).filter(test4) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("normalize carriage return in descriptor test name") {
      val spec = NestedTestsArgDescriptorFilterTest::class.toDescriptor()
      // descriptor has a raw \r; the Gradle --tests arg has been normalized (\r -> space)
      // Note: \n is rejected by DescriptorId validation, but \r is allowed
      val test1 = spec.append("a\rb")
      val test2 = test1.append("c")
      val args = NestedTestsArgParser.parse("\\Qio.kotest.runner.junit.platform.gradle.NestedTestsArgDescriptorFilterTest.a b -- c\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args)).filter(test1) shouldBe DescriptorFilterResult.Include
      NestedTestsArgDescriptorFilter(setOf(args)).filter(test2) shouldBe DescriptorFilterResult.Include
   }

   test("normalize surrounding whitespace in descriptor test name") {
      val spec = NestedTestsArgDescriptorFilterTest::class.toDescriptor()
      val test1 = spec.append("  a  ")
      val test2 = test1.append("b")
      val args = NestedTestsArgParser.parse("\\Qio.kotest.runner.junit.platform.gradle.NestedTestsArgDescriptorFilterTest.a -- b\\E")!!
      NestedTestsArgDescriptorFilter(setOf(args)).filter(test1) shouldBe DescriptorFilterResult.Include
      NestedTestsArgDescriptorFilter(setOf(args)).filter(test2) shouldBe DescriptorFilterResult.Include
   }
})

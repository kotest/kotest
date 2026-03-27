package com.sksamuel.kotest.engine.extensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.engine.extensions.filter.IncludePatternEnvDescriptorFilter
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class IncludeTestPatternDescriptorFilterTest : FunSpec({

   test("include matching packages") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludePatternEnvDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.extensions.IncludeTestPatternDescriptorFilterTest.a -- b",
         spec
      ) shouldBe DescriptorFilterResult.Include
   }

   test("include matching subpackages if wildcard") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludePatternEnvDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.*",
         spec
      ) shouldBe DescriptorFilterResult.Include
   }

   test("exclude non matching subpackages if not wildcard") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludePatternEnvDescriptorFilter.filter(
         "com.sksamuel.kotest.engine",
         spec
      ) shouldBe DescriptorFilterResult.Exclude(null)
   }


   test("exclude non matching packages") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludePatternEnvDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.xxxx.IncludeTestPatternDescriptorFilterTest.a -- b",
         spec
      ) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include matching classes") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludePatternEnvDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.extensions.IncludeTestPatternDescriptorFilterTest",
         spec
      ) shouldBe DescriptorFilterResult.Include
      IncludePatternEnvDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.extensions.IncludeTestPatternDescriptorFilterTest.a -- b",
         spec
      ) shouldBe DescriptorFilterResult.Include
   }

   test("exclude non matching classes") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludePatternEnvDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.extensions.SomeSpec",
         spec
      ) shouldBe DescriptorFilterResult.Exclude(null)
      IncludePatternEnvDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.extensions.SomeSpec.a -- b",
         spec
      ) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include matching tests") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      val test1 = spec.append("a")
      val test2 = test1.append("b")
      val test3 = spec.append("c")
      val test4 = test1.append("d")
      val pattern = "com.sksamuel.kotest.engine.extensions.IncludeTestPatternDescriptorFilterTest.a -- b"
      IncludePatternEnvDescriptorFilter.filter(pattern, test1) shouldBe DescriptorFilterResult.Include
      IncludePatternEnvDescriptorFilter.filter(pattern, test2) shouldBe DescriptorFilterResult.Include
      IncludePatternEnvDescriptorFilter.filter(pattern, test3) shouldBe DescriptorFilterResult.Exclude(null)
      IncludePatternEnvDescriptorFilter.filter(pattern, test4) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include tests whose names contain CR when pattern uses space") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      val test1 = spec.append("a\rtest")
      val pattern = "com.sksamuel.kotest.engine.extensions.IncludeTestPatternDescriptorFilterTest.a test"
      IncludePatternEnvDescriptorFilter.filter(pattern, test1) shouldBe DescriptorFilterResult.Include
   }

   context("question-mark wildcard in test name matches tests with periods") {
      // GradleTestFilterBuilder replaces '.' in test names with '?' to avoid Gradle
      // misinterpreting them as FQN separators. This filter must treat '?' as a
      // single-character wildcard so that e.g. "1?2?3 my test" matches "1.2.3 my test".

      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      val fqcn = "com.sksamuel.kotest.engine.extensions.IncludeTestPatternDescriptorFilterTest"

      test("root test named '1.2.3 my test' is INCLUDED by pattern with question marks") {
         val testDescriptor = spec.append("1.2.3 my test")
         IncludePatternEnvDescriptorFilter.filter("$fqcn.1?2?3 my test", testDescriptor) shouldBe DescriptorFilterResult.Include
      }

      test("root test named '1.2.3 my test' is EXCLUDED by non-matching question-mark pattern") {
         val testDescriptor = spec.append("1.2.3 my test")
         IncludePatternEnvDescriptorFilter.filter("$fqcn.4?5?6 my test", testDescriptor) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("spec is INCLUDED when filtering to a test with question-mark pattern") {
         IncludePatternEnvDescriptorFilter.filter("$fqcn.1?2?3 my test", spec) shouldBe DescriptorFilterResult.Include
      }

      test("child of '1.2.3 my test' is INCLUDED when filtering to parent with question marks") {
         val parent = spec.append("1.2.3 my test")
         val child = parent.append("nested child")
         IncludePatternEnvDescriptorFilter.filter("$fqcn.1?2?3 my test", child) shouldBe DescriptorFilterResult.Include
      }

      test("nested test with periods in name is INCLUDED by question-mark pattern") {
         val parent = spec.append("v1.0 context")
         val child = parent.append("feature 2.0")
         IncludePatternEnvDescriptorFilter.filter("$fqcn.v1?0 context -- feature 2?0", child) shouldBe DescriptorFilterResult.Include
      }

      test("question mark does not match multiple characters") {
         val testDescriptor = spec.append("1..2 my test")
         // '?' matches exactly one char, so "1?2" should NOT match "1..2"
         IncludePatternEnvDescriptorFilter.filter("$fqcn.1?2 my test", testDescriptor) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }
})

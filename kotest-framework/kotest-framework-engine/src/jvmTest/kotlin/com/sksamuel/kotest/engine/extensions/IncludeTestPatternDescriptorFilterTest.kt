package com.sksamuel.kotest.engine.extensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.engine.extensions.filter.IncludeTestPatternDescriptorFilter
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class IncludeTestPatternDescriptorFilterTest : FunSpec({

   test("include matching packages") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludeTestPatternDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.extensions.IncludeTestPatternDescriptorFilterTest.a -- b",
         spec
      ) shouldBe DescriptorFilterResult.Include
   }

   test("include matching subpackages if wildcard") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludeTestPatternDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.*",
         spec
      ) shouldBe DescriptorFilterResult.Include
   }

   test("exclude non matching subpackages if not wildcard") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludeTestPatternDescriptorFilter.filter(
         "com.sksamuel.kotest.engine",
         spec
      ) shouldBe DescriptorFilterResult.Exclude(null)
   }


   test("exclude non matching packages") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludeTestPatternDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.xxxx.IncludeTestPatternDescriptorFilterTest.a -- b",
         spec
      ) shouldBe DescriptorFilterResult.Exclude(null)
   }

   test("include matching classes") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludeTestPatternDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.extensions.IncludeTestPatternDescriptorFilterTest",
         spec
      ) shouldBe DescriptorFilterResult.Include
      IncludeTestPatternDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.extensions.IncludeTestPatternDescriptorFilterTest.a -- b",
         spec
      ) shouldBe DescriptorFilterResult.Include
   }

   test("exclude non matching classes") {
      val spec = IncludeTestPatternDescriptorFilterTest::class.toDescriptor()
      IncludeTestPatternDescriptorFilter.filter(
         "com.sksamuel.kotest.engine.extensions.SomeSpec",
         spec
      ) shouldBe DescriptorFilterResult.Exclude(null)
      IncludeTestPatternDescriptorFilter.filter(
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
      IncludeTestPatternDescriptorFilter.filter(pattern, test1) shouldBe DescriptorFilterResult.Include
      IncludeTestPatternDescriptorFilter.filter(pattern, test2) shouldBe DescriptorFilterResult.Include
      IncludeTestPatternDescriptorFilter.filter(pattern, test3) shouldBe DescriptorFilterResult.Exclude(null)
      IncludeTestPatternDescriptorFilter.filter(pattern, test4) shouldBe DescriptorFilterResult.Exclude(null)


   }
})

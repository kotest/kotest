package io.kotest.runner.junit.platform.gradle

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GradleClassMethodRegexTestFilterTest : FunSpec({

   test("includes") {

      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()

      GradleClassMethodRegexTestFilter(listOf("GradleClassMethodRegexTestFilterTest")).filter(spec) shouldBe TestFilterResult.Include
      GradleClassMethodRegexTestFilter(listOf("GradleClassMethodRegexTestFilterTest2")).filter(spec) shouldBe
         TestFilterResult.Exclude(null)

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilterTest"))
         .filter(spec) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.GradleClassMethodRegexTestFilterTest"))
         .filter(spec) shouldBe TestFilterResult.Exclude(null)
   }

   test("includes with test paths") {

      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val container = spec.append("a context")
      val test = container.append("nested test")

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilterTest.a context"))
         .filter(container) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilterTest.a context2"))
         .filter(container) shouldBe TestFilterResult.Exclude(null)

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilterTest.a context -- nested test"))
         .filter(test) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilterTest.nested test"))
         .filter(test) shouldBe TestFilterResult.Exclude(null)

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilterTest.a context -- nested test2"))
         .filter(test) shouldBe TestFilterResult.Exclude(null)
   }
})

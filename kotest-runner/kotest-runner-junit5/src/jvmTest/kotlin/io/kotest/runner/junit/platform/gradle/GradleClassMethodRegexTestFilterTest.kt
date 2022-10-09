package io.kotest.runner.junit.platform.gradle

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class GradleClassMethodRegexTestFilterTest : FunSpec({

   test("include classes") {

      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()

      GradleClassMethodRegexTestFilter(listOf("GradleClassMethodRegexTestFilterTest")).filter(spec) shouldBe
         TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("*thodRegexTestFilterTest")).filter(spec) shouldBe
         TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("GradleClassMethodRegexTestFilterTest2")).filter(spec) shouldBe
         TestFilterResult.Exclude(null)

      GradleClassMethodRegexTestFilter(listOf("GradleClassMethodRegexTestFilterTes")).filter(spec) shouldBe
         TestFilterResult.Exclude(null)

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilterTest"))
         .filter(spec) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("*orm.gradle.GradleClassMethodRegexTestFilterTest"))
         .filter(spec) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("*.platform.gradle.GradleClassMethodRegexTestFilterTest"))
         .filter(spec) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.GradleClassMethodRegexTestFilterTest"))
         .filter(spec) shouldBe TestFilterResult.Exclude(null)
   }

   context("include packages") {

      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val container = spec.append("a context")
      val test = container.append("nested test")

      test("Exact match - includes the spec and tests within it") {
         val filter = GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.gradle"))

         filter.filter(spec) shouldBe TestFilterResult.Include
         filter.filter(test) shouldBe TestFilterResult.Include
      }

      GradleClassMethodRegexTestFilter(listOf("*nner.junit.platform.gradle"))
         .filter(spec) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("*.junit.platform.gradle"))
         .filter(spec) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit.platform.gra"))
         .filter(spec) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit"))
         .filter(spec) shouldBe TestFilterResult.Include

      GradleClassMethodRegexTestFilter(listOf("io.kotest.runner.junit2"))
         .filter(spec) shouldBe TestFilterResult.Exclude(null)
   }

   context("includes with test paths") {

      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val container = spec.append("a context")
      val test = container.append("nested test")
      val fqcn = GradleClassMethodRegexTestFilterTest::class.qualifiedName


      withData(
         nameFn = { "should be INCLUDED when filter is: $it" },
         "$fqcn",
         "$fqcn.a context",
         "*.gradle.GradleClassMethodRegexTestFilterTest.a context",
         "*adle.GradleClassMethodRegexTestFilterTest.a context",
         "$fqcn.a context -- nested test",
      ) { filter ->
         GradleClassMethodRegexTestFilter(listOf(filter))
            .filter(test) shouldBe TestFilterResult.Include
      }

      withData(
         nameFn = { "should be EXCLUDED when filter is: $it" },
         "$fqcn.a context2",
         "$fqcn.nested test",
         "$fqcn.a context -- nested test2",
         "*sMethodRegexTestFilterTest.a context -- nested test2",
      ) { filter ->
         GradleClassMethodRegexTestFilter(listOf(filter))
            .filter(test) shouldBe TestFilterResult.Exclude(null)
      }
   }
})

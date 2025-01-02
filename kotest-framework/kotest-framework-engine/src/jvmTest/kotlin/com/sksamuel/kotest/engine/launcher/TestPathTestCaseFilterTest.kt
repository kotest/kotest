package com.sksamuel.kotest.engine.launcher

import io.kotest.common.TestPath
import io.kotest.core.descriptors.append
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.launcher.TestPathTestCaseFilter
import io.kotest.engine.test.path.TestPathBuilder
import io.kotest.matchers.shouldBe

class TestPathTestCaseFilterTest : FunSpec() {
   init {

      test("filter should exclude tests in a different spec") {
         TestPathTestCaseFilter(TestPath("foo"), Spec1::class).filter(
            Spec2::class.toDescriptor().append("foo")
         ) shouldBe TestFilterResult.Exclude("Excluded by test path filter: 'foo'")
      }

      test("filter should exclude tests in the same spec with a different name") {
         TestPathTestCaseFilter(TestPath("foo"), Spec1::class).filter(
            Spec1::class.toDescriptor().append("boo")
         ) shouldBe TestFilterResult.Exclude("Excluded by test path filter: 'foo'")
      }

      test("filter should include tests matching name and spec") {
         TestPathTestCaseFilter(TestPath("foo"), Spec1::class).filter(Spec1::class.toDescriptor().append("foo")) shouldBe TestFilterResult.Include
      }

      test("filter should include child tests of the target") {
         TestPathTestCaseFilter(TestPath("foo"), Spec1::class).filter(Spec1::class.toDescriptor().append("foo").append("bar")) shouldBe TestFilterResult.Include
      }

      test("filter should include parent tests of the target") {
         TestPathTestCaseFilter(TestPathBuilder.parse("foo -- bar"), Spec1::class).filter(
            Spec1::class.toDescriptor().append("foo")
         ) shouldBe TestFilterResult.Include
      }

      test("filter should include the target spec") {
         TestPathTestCaseFilter(
            TestPathBuilder.parse("foo -- bar"),
            Spec1::class
         ).filter(Spec1::class.toDescriptor()) shouldBe TestFilterResult.Include
      }

      test("filter should exclude another spec with same test name") {
         TestPathTestCaseFilter(
            TestPathBuilder.parse("foo -- bar"),
            Spec1::class
         ).filter(Spec2::class.toDescriptor()) shouldBe TestFilterResult.Exclude("Excluded by test path filter: 'foo -- bar'")
      }

      test("filter should work for word spec") {

         TestPathTestCaseFilter(TestPathBuilder.parse("a container -- pass a test"), WordSpec1::class).filter(
            WordSpec1::class.toDescriptor().append("a container should").append("pass a test")
         ) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter(TestPathBuilder.parse("a container -- pass a test"), WordSpec1::class).filter(
            WordSpec1::class.toDescriptor().append("a container should").append("skip a test")
         ) shouldBe TestFilterResult.Exclude("Excluded by test path filter: 'a container -- pass a test'")
      }

      test("filter should work for word spec with when") {

         TestPathTestCaseFilter(TestPathBuilder.parse("a when"), WordSpec2::class).filter(
            WordSpec2::class.toDescriptor().append("a when")
         ) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter(TestPathBuilder.parse("a when -- a should"), WordSpec2::class).filter(
            WordSpec2::class.toDescriptor().append("a when").append("a should")
         ) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter(TestPathBuilder.parse("a when -- a should"), WordSpec2::class).filter(
            WordSpec2::class.toDescriptor().append("a when").append("a shouldnt")
         ) shouldBe TestFilterResult.Exclude("Excluded by test path filter: 'a when -- a should'")

         TestPathTestCaseFilter(TestPathBuilder.parse("a when -- a should -- a test"), WordSpec2::class).filter(
            WordSpec2::class.toDescriptor().append("a when").append("a should").append("a test")
         ) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter(TestPathBuilder.parse("a when -- a should -- a test"), WordSpec2::class).filter(
            WordSpec2::class.toDescriptor().append("a when").append("a should").append("boo")
         ) shouldBe TestFilterResult.Exclude("Excluded by test path filter: 'a when -- a should -- a test'")
      }

      test("filter should trim whitespace from names") {

         TestPathTestCaseFilter(TestPathBuilder.parse("    a container   "), WordSpec1::class).filter(
            WordSpec1::class.toDescriptor().append("a container should").append("pass a test")
         ) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter(TestPathBuilder.parse("    a container -- pass a test"), WordSpec1::class).filter(
            WordSpec1::class.toDescriptor().append("a container should").append("pass a test")
         ) shouldBe TestFilterResult.Include
      }
   }
}

private class Spec1 : StringSpec() {
   init {
      "foo"{}
      "boo"{}
   }
}

private class Spec2 : StringSpec() {
   init {
      "foo"{}
      "boo"{}
   }
}

private class WordSpec1 : WordSpec() {
   init {
      "a container" should {
         "skip a test".config(enabled = false) {}
         "pass a test" { 1 shouldBe 1 }
      }
   }
}

private class WordSpec2 : WordSpec() {
   init {
      "a when" When {
         "a should" should {
            "a test" { }
         }
      }
   }
}

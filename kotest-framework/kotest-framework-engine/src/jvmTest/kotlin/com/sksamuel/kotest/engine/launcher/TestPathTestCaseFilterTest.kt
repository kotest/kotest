package com.sksamuel.kotest.engine.launcher

import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.plan.toDescriptor
import io.kotest.engine.launcher.TestPathTestFilter
import io.kotest.matchers.shouldBe

class TestPathTestCaseFilterTest : FunSpec() {
   init {
      test("filter should exclude tests in a different spec") {
         TestPathTestFilter("foo", Spec1::class).filter(Spec2::class.toDescriptor().appendTest("foo")) shouldBe TestFilterResult.Exclude
      }
      test("filter should exclude tests in the same spec with a different name") {
         TestPathTestFilter("foo", Spec1::class).filter(Spec1::class.toDescriptor().appendTest("boo")) shouldBe TestFilterResult.Exclude
      }
      test("filter should include tests matching name and spec") {
         TestPathTestFilter("foo", Spec1::class).filter(Spec1::class.toDescriptor().appendTest("foo")) shouldBe TestFilterResult.Include
      }
      test("filter should include child tests of the target") {
         TestPathTestFilter("foo", Spec1::class).filter(Spec1::class.toDescriptor().appendTest("foo").appendTest("bar")) shouldBe TestFilterResult.Include
      }
      test("filter should include parent tests of the target") {
         TestPathTestFilter("foo -- bar", Spec1::class).filter(
            Spec1::class.toDescriptor().appendTest("foo")
         ) shouldBe TestFilterResult.Include
      }
      test("filter should include the target spec") {
         TestPathTestFilter(
            "foo -- bar",
            Spec1::class
         ).filter(Spec1::class.toDescriptor()) shouldBe TestFilterResult.Include
      }
      test("filter should exclude another spec") {
         TestPathTestFilter(
            "foo -- bar",
            Spec1::class
         ).filter(Spec2::class.toDescriptor()) shouldBe TestFilterResult.Exclude
      }
      test("filter should work for word spec") {
         TestPathTestFilter("a container -- pass a test", WordSpec1::class).filter(
            WordSpec1::class.toDescriptor().appendTest("a container should").appendTest("pass a test")
         ) shouldBe TestFilterResult.Include
         TestPathTestFilter("a container -- pass a test", WordSpec1::class).filter(
            WordSpec1::class.toDescriptor().appendTest("a container should").appendTest("skip a test")
         ) shouldBe TestFilterResult.Exclude
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

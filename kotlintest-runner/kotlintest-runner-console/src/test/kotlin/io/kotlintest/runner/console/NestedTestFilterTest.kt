package io.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.TestFilterResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class NestedTestFilterTest : FunSpec() {

  init {

    test("should only include the exact test without context") {
      NestedTestFilter("test one").filter(Description.spec("my spec").append("test")) shouldBe TestFilterResult.Exclude
      NestedTestFilter("test one").filter(Description.spec("my spec").append("test one")) shouldBe TestFilterResult.Include
      NestedTestFilter("test").filter(Description.spec("my spec").append("test")) shouldBe TestFilterResult.Include
      NestedTestFilter("test").filter(Description.spec("my spec").append("test one")) shouldBe TestFilterResult.Exclude
    }

    test("should include parent tests with context") {
      NestedTestFilter("context / test one").filter(Description.spec("my spec").append("context")) shouldBe TestFilterResult.Include
    }

    test("should include exact tests with a context") {
      NestedTestFilter("context / test one").filter(Description.spec("my spec").append("context").append("test")) shouldBe TestFilterResult.Exclude
      NestedTestFilter("context / test one").filter(Description.spec("my spec").append("context").append("test one")) shouldBe TestFilterResult.Include
      NestedTestFilter("context / test one").filter(Description.spec("my spec").append("test one")) shouldBe TestFilterResult.Exclude
      NestedTestFilter("context / test").filter(Description.spec("my spec").append("context").append("test")) shouldBe TestFilterResult.Include
      NestedTestFilter("context / test").filter(Description.spec("my spec").append("context").append("test one")) shouldBe TestFilterResult.Exclude
      NestedTestFilter("context / test").filter(Description.spec("my spec").append("test one")) shouldBe TestFilterResult.Exclude
    }
  }
}
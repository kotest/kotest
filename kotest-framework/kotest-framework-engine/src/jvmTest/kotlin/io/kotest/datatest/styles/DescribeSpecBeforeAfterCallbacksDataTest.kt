package io.kotest.datatest.styles

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecBeforeAfterCallbacksDataTest : DescribeSpec() {
   init {

      duplicateTestNameMode = DuplicateTestNameMode.Silent

      var beforeTest = 0
      var beforeContainer = 0
      var afterContainer = 0
      var afterTest = 0
      var beforeEach = 0
      var afterEach = 0

      beforeTest {
         beforeTest++
      }
      beforeContainer {
         beforeContainer++
      }
      beforeEach {
         beforeEach++
      }
      afterEach {
         afterEach++
      }
      afterContainer {
         afterContainer++
      }
      afterTest {
         afterTest++
      }
      afterSpec {
         beforeTest shouldBe 13
         beforeContainer shouldBe 7
         beforeEach shouldBe 6
         afterEach shouldBe 6
         afterContainer shouldBe 7
         afterTest shouldBe 13
      }

      withData(
         "foo",
         "bar",
         "baz"
      ) {
         it("test") {}
      }

      describe("inside a context") {
         withData(
            "foo",
            "bar",
            "baz"
         ) {
            it("test") {}
         }
      }
   }
}

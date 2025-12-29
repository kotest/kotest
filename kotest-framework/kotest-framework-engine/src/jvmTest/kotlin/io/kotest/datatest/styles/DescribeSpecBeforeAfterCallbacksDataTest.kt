package io.kotest.datatest.styles

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withDescribes
import io.kotest.datatest.withIts
import io.kotest.matchers.shouldBe

class DescribeSpecBeforeAfterCallbacksDataTest : DescribeSpec() {
   init {

      duplicateTestNameMode = DuplicateTestNameMode.Silent

      var beforeAnyTest = 0
      var beforeTest = 0
      var beforeContainer = 0
      var afterContainer = 0
      var afterTest = 0
      var beforeEach = 0
      var afterEach = 0

      beforeAny {
         beforeAnyTest++
      }
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
         beforeTest shouldBe 32
         beforeAnyTest shouldBe 32
         beforeContainer shouldBe 12
         beforeEach shouldBe 20
         afterEach shouldBe 20
         afterContainer shouldBe 12
         afterTest shouldBe 32
      }

      withContexts(
         "foo",
         "bar",
         "baz"
      ) {
         withIts("test 1", "test 2") {
         }
      }

      withDescribes(
         "foo",
         "bar",
         "baz"
      ) {
         withIts("test 1", "test 2") {
         }
      }


      withDescribes(
         "inside describe context 1",
         "inside describe context 1"
      ) {
         withContexts(
            "inside context within describe context 1",
            "inside context within describe context 2"
         ) {
            withIts(
               "final test within each describe and context declared above - 1",
               "final test within each describe and context declared above - 1"
            ) { }
         }
      }
   }
}

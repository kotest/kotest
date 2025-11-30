package io.kotest.datatest.styles

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withData
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe

class FunSpecBeforeAfterCallbacksDataTest : FunSpec() {
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
         beforeTest shouldBe 57
         beforeContainer shouldBe 15
         beforeEach shouldBe 42
         afterEach shouldBe 42
         afterContainer shouldBe 15
         afterTest shouldBe 57
      }

      withContexts(
         "foo",
         "bar",
         "baz"
      ) {
         withTests("it1","it2") {}
      }

      context("inside a context") {
         withContexts(
            "foo",
            "bar",
            "baz"
         ) {
            withTests("it1","it2") {}
            withTests("it3","it4") {}
         }
      }

      withContexts("inside a context 1", "inside a context 2") {
         withContexts(
            "foo",
            "bar",
            "baz"
         ) {
            withTests("it1","it2") {}
            withTests("it3","it4") {}
         }
      }
   }
}

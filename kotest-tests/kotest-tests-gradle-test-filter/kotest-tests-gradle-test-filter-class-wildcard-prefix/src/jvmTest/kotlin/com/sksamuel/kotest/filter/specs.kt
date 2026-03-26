package com.sksamuel.kotest.filter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

var tests = 0

// matches the filter '*Foo'
class Foo : FunSpec() {
   init {
      afterProject {
         tests shouldBe 1
      }
      test("a") { tests++ }
   }
}

// should be ignored completely as the filter is '*Foo'
class Bar : FunSpec() {
   init {
      test("whack!") {
         error("whack!")
      }
   }
}

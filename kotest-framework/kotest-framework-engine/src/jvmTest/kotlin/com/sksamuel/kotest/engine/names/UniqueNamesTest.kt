package com.sksamuel.kotest.engine.names

import io.kotest.core.names.UniqueNames
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UniqueNamesTest : FunSpec() {
   init {
      test("should make repeated names unique") {
         UniqueNames.uniqueTestName("foo", emptySet()) shouldBe null
         UniqueNames.uniqueTestName("foo", setOf("foo")) shouldBe "(1) foo"
         UniqueNames.uniqueTestName("foo", setOf("foo", "(1) foo")) shouldBe "(2) foo"
         UniqueNames.uniqueTestName("foo", setOf("foo", "(1) foo", "(2) foo")) shouldBe "(3) foo"
         UniqueNames.uniqueTestName("foo", setOf("foo", "(1) foo")) shouldBe "(2) foo"
      }
   }
}

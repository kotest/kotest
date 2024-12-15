package com.sksamuel.kotest.engine.names

import io.kotest.engine.names.UniqueNames
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UniqueNamesTest : FunSpec() {
   init {
      test("should make repeated names unique") {
         UniqueNames.unique("foo", emptySet()) shouldBe null
         UniqueNames.unique("foo", setOf("foo")) shouldBe "(1) foo"
         UniqueNames.unique("foo", setOf("foo", "(1) foo")) shouldBe "(2) foo"
         UniqueNames.unique("foo", setOf("foo", "(1) foo", "(2) foo")) shouldBe "(3) foo"
         UniqueNames.unique("foo", setOf("foo", "(1) foo")) shouldBe "(2) foo"
      }
   }
}

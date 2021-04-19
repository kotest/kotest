package io.kotest.core.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IdentifiersTest : FunSpec() {
   init {
      test("should make repeated names unique") {
         Identifiers.uniqueTestName("foo", emptySet()) shouldBe "foo"
         Identifiers.uniqueTestName("foo", setOf("foo")) shouldBe "foo (1)"
         Identifiers.uniqueTestName("foo", setOf("foo", "foo (1)")) shouldBe "foo (2)"
         Identifiers.uniqueTestName("foo", setOf("foo", "foo (1)", "foo (2)")) shouldBe "foo (3)"
         Identifiers.uniqueTestName("foo", setOf("foo", "foo (1")) shouldBe "foo (1)"
      }
   }
}

package com.sksamuel.kotest.property

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class ClassifyTest : StringSpec() {
   init {
      "classify should log passing predicates" {

         forAll<Int>(PropTestConfig(seed = 1234)) { a ->
            classify(a == 0, "zero")
            classify(a % 2 == 0, "even number", "odd number")
            a + a == 2 * a
         }.classifications() shouldBe mapOf("zero" to 1, "even number" to 499, "odd number" to 501)

         forAll(PropTestConfig(seed = 1234), Arb.string()) { a ->
            classify(a.contains(" "), "has whitespace", "no whitespace")
            a + "" == "" + a
         }.classifications() shouldBe mapOf("no whitespace" to 621, "has whitespace" to 379)
      }
   }
}

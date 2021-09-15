//package com.sksamuel.kotest.engine
//
//import io.kotest.core.script.context
//import io.kotest.core.script.describe
//import io.kotest.core.script.should
//import io.kotest.core.script.test
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.string.shouldHaveLength
//import io.kotest.matchers.string.shouldHaveLengthBetween
//
//test("this is a fun spec style") {
//   1 + 2 shouldBe 3
//}
//
//should("this is a should spec style") {
//   "foo".shouldHaveLength(3)
//}
//
//describe("this is a describe spec context") {
//   it("and this is a describe spec test") {
//      "a" + "b" shouldBe "ab"
//   }
//}
//
//context("this is a context scope") {
//   test("a nested test") {
//      "bar".shouldHaveLengthBetween(2, 4)
//   }
//   should("a nested should") {
//      "foo".shouldHaveLength(3)
//   }
//}

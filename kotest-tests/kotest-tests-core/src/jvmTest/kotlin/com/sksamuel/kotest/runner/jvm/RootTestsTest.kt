//package com.sksamuel.kotest.runner.jvm
//
//import io.kotest.core.TestCaseOrder
//import io.kotest.internal.rootTests
//import io.kotest.specs.FunSpec
//import io.kotest.specs.StringSpec
// todo restore this !
//
//class RootTestsTest : FunSpec({
//
//  test("spec sequential test ordering") {
//    val tests = rootTests(SequentialSpec())
//    tests.tests.map { it.testCase.name } shouldBe listOf("a", "b", "c", "d", "e")
//  }
//
//  test("spec randomized test ordering") {
//    val tests1 = rootTests(RandomSpec())
//    val tests2 = rootTests(RandomSpec())
//    tests1.tests.map { it.testCase.name } shouldNotBe tests2.tests.map { it.testCase.name }
//  }
//
//})
//
//class SequentialSpec : StringSpec() {
//  override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential
//
//  init {
//    "a" {}
//    "b" {}
//    "c" {}
//    "d" {}
//    "e" {}
//  }
//}
//
//class RandomSpec : StringSpec() {
//  override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Random
//
//  init {
//    "a" {}
//    "b" {}
//    "c" {}
//    "d" {}
//    "e" {}
//    "f" {}
//    "g" {}
//    "h" {}
//    "i" {}
//    "j" {}
//    "k" {}
//  }
//}

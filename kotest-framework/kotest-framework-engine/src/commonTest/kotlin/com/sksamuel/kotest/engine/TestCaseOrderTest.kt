package com.sksamuel.kotest.engine

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.config.DefaultTestConfig
import io.kotest.engine.spec.Materializer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TestCaseOrderTest : FunSpec() {
   init {
      test("sequential test case ordering specified in the spec") {
         Materializer(ProjectConfiguration()).materialize(SequentialSpec()).map { it.name.name } shouldBe
            listOf("c", "b", "d", "e", "a")
      }
      test("Lexicographic test case ordering specified in the spec") {
         Materializer(ProjectConfiguration()).materialize(LexicographicSpec()).map { it.name.name } shouldBe
            listOf("a", "b", "c", "d", "e")
      }
      test("random test case ordering specified in the spec") {
         val a = Materializer(ProjectConfiguration()).materialize(RandomSpecByMethodOverride()).map { it.name.name }
         val b = Materializer(ProjectConfiguration()).materialize(RandomSpecByMethodOverride()).map { it.name.name }
         a shouldNotBe b
      }
      test("random test case ordering specified in default test case") {
         val a = Materializer(ProjectConfiguration()).materialize(RandomSpecByDefaultConfig()).map { it.name.name }
         val b = Materializer(ProjectConfiguration()).materialize(RandomSpecByDefaultConfig()).map { it.name.name }
         a shouldNotBe b
      }
      test("sequential test case ordering specified in project config") {
         val c = ProjectConfiguration()
         c.testCaseOrder = TestCaseOrder.Sequential
         Materializer(c).materialize(UnspecifiedSpec()).map { it.name.name } shouldBe
            listOf("d", "b", "c", "e", "h", "f", "g", "i", "a", "l", "j", "k", "m", "p", "n", "o", "q", "r")
      }
      test("Lexicographic test case ordering specified in project config") {
         val c = ProjectConfiguration()
         c.testCaseOrder = TestCaseOrder.Lexicographic
         Materializer(c).materialize(UnspecifiedSpec()).map { it.name.name } shouldBe
            listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r")
      }
      test("random test case ordering specified in project config") {
         val c = ProjectConfiguration()
         c.testCaseOrder = TestCaseOrder.Random
         val a = Materializer(c).materialize(UnspecifiedSpec()).map { it.name.name }
         val b = Materializer(c).materialize(UnspecifiedSpec()).map { it.name.name }
         a shouldNotBe b
      }
   }
}

class SequentialSpec : StringSpec() {

   override fun testCaseOrder() = TestCaseOrder.Sequential

   init {
      "c" {}
      "b" {}
      "d" {}
      "e" {}
      "a" {}
   }
}

private class LexicographicSpec : StringSpec() {

   override fun testCaseOrder() = TestCaseOrder.Lexicographic

   init {
      "b" {}
      "d" {}
      "a" {}
      "e" {}
      "c" {}
   }
}

private class RandomSpecByMethodOverride : StringSpec() {

   override fun testCaseOrder() = TestCaseOrder.Random

   init {
      "a" {}
      "b" {}
      "c" {}
      "d" {}
      "e" {}
      "f" {}
      "g" {}
      "h" {}
      "i" {}
      "j" {}
      "k" {}
      "l" {}
      "m" {}
      "n" {}
      "o" {}
      "p" {}
      "q" {}
      "r" {}
      "s" {}
      "t" {}
      "u" {}
      "v" {}
   }
}

private class RandomSpecByDefaultConfig : StringSpec() {

   init {

      defaultTestConfig = DefaultTestConfig(testOrder = TestCaseOrder.Random)

      "a" {}
      "b" {}
      "c" {}
      "d" {}
      "e" {}
      "f" {}
      "g" {}
      "h" {}
      "i" {}
      "j" {}
      "k" {}
      "l" {}
      "m" {}
      "n" {}
      "o" {}
      "p" {}
      "q" {}
      "r" {}
      "s" {}
      "t" {}
      "u" {}
      "v" {}
   }
}

private class UnspecifiedSpec : StringSpec() {

   init {
      "d" {}
      "b" {}
      "c" {}
      "e" {}
      "h" {}
      "f" {}
      "g" {}
      "i" {}
      "a" {}
      "l" {}
      "j" {}
      "k" {}
      "m" {}
      "p" {}
      "n" {}
      "o" {}
      "q" {}
      "r" {}
   }
}

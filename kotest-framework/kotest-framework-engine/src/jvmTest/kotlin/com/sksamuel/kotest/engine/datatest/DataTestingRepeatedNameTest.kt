package com.sksamuel.kotest.engine.datatest

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.shouldBe

class DataTestingRepeatedTestNameTest : FunSpec() {
   init {

      val names = mutableListOf<String>()

      val listener = object : TestEngineListener {
         override fun testFinished(testCase: TestCase, result: TestResult) {
            println(testCase.displayName)
            names.add(testCase.displayName)
         }
      }

      beforeTest {
         names.clear()
      }

      test("repeated names should have count appended") {

         KotestEngineLauncher()
            .withListener(listener)
            .withSpec(RepeatedNameTest::class)
            .launch()

         names shouldBe listOf(
            "Foo(name=sam)",
            "Foo(name=ham)",
            "Foo(name=sham)",
            "Foo(name=sham) (1)",
            "Foo(name=ham) (1)",
            "Foo(name=ham) (2)",
            "foo",
         )
      }
   }
}

private class RepeatedNameTest : DescribeSpec() {
   init {
      describe("foo") {
         forAll(
            Foo("sam"),
            Foo("ham"),
            Foo("sham"),
            Foo("sham"),
            Foo("ham"),
            Foo("ham"),
         ) { }
      }
   }
}

private data class Foo(val name: String)

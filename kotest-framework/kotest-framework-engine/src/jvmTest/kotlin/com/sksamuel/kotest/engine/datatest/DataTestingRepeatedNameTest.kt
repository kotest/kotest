package com.sksamuel.kotest.engine.datatest

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.shouldBe

@Isolate // sets global values via configuration so must be isolated
class DataTestingRepeatedTestNameTest : FunSpec() {
   init {

      val names = mutableListOf<String>()

      val listener = object : AbstractTestEngineListener() {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            println(testCase.name.testName)
            names.add(testCase.name.testName)
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
            "(1) Foo(name=sham)",
            "(1) Foo(name=ham)",
            "(2) Foo(name=ham)",
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

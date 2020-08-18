package com.sksamuel.kotest.framework.console

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.system.captureStandardOut
import io.kotest.framework.console.BasicConsoleWriter
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder

class BasicConsoleWriterTest : FunSpec() {

   init {

      val desc = BasicConsoleWriterTest::class.toDescription()

      test("final write should include summary info") {

         val test1 = TestCase.test(desc.appendTest("a test"), this@BasicConsoleWriterTest) {}
         val test2 = TestCase.test(desc.appendTest("b test"), this@BasicConsoleWriterTest) {}
         val test3 = TestCase.test(desc.appendTest("c test"), this@BasicConsoleWriterTest) {}
         val test4 = TestCase.test(desc.appendTest("d test"), this@BasicConsoleWriterTest) {}
         val test5 = TestCase.test(desc.appendTest("e test"), this@BasicConsoleWriterTest) {}

         val out = captureStandardOut {
            val writer = BasicConsoleWriter()
            writer.engineStarted(emptyList())
            writer.specStarted(this@BasicConsoleWriterTest::class)
            writer.testStarted(test1)
            writer.testFinished(test1, TestResult.success(0))
            writer.testStarted(test2)
            writer.testFinished(test2, TestResult.error(RuntimeException("wibble boom"), 0))
            writer.testStarted(test3)
            writer.testFinished(test3, TestResult.failure(AssertionError("wobble vablam"), 0))
            writer.testStarted(test4)
            writer.testFinished(test4, TestResult.ignored("don't like it"))
            writer.testStarted(test5)
            writer.testFinished(test5, TestResult.success(0))
            writer.specFinished(this@BasicConsoleWriterTest::class, null, emptyMap())
            writer.engineFinished(emptyList())
         }

         out.shouldContain("com.sksamuel.kotest.framework.console.BasicConsoleWriterTest")
         out.shouldContain("\ta test")
         out.shouldContain("\tcause: wibble boom (BasicConsoleWriterTest.kt:21)")
         out.shouldContain("Kotest completed in")
         out.shouldContain("Specs: completed 1, tests 5")
         out.shouldContain("Tests: passed 2, failed 2, ignored 1")
         out.shouldContain("*** 2 TESTS FAILED ***")
         out.shouldContain("Specs with failing tests:")
         out.shouldContain(" - com.sksamuel.kotest.framework.console.BasicConsoleWriterTest")

      }

      test("tests should be outputted in nested order") {

         val test1 =
            TestCase.container(desc.appendTest("first test"), this@BasicConsoleWriterTest) {}

         val test2 =
            TestCase.container(
               desc.appendTest("first test").appendTest("second test"),
               this@BasicConsoleWriterTest
            ) {}

         val test3 = TestCase.test(
            desc.appendTest("first test").appendTest("second test").appendTest("third test"),
            this@BasicConsoleWriterTest
         ) {}

         val test4 =
            TestCase.container(desc.appendTest("fourth test"), this@BasicConsoleWriterTest) {}

         val test5 = TestCase.test(
            desc.appendTest("fourth test").appendTest("fifth test"),
            this@BasicConsoleWriterTest
         ) {}

         val out = captureStandardOut {
            val writer = BasicConsoleWriter()
            writer.engineStarted(emptyList())
            writer.specStarted(this@BasicConsoleWriterTest::class)
            writer.testStarted(test1)
            writer.testStarted(test2)
            writer.testStarted(test3)
            writer.testFinished(test3, TestResult.failure(AssertionError("wobble vablam"), 0))
            writer.testFinished(test2, TestResult.error(RuntimeException("wibble boom"), 0))
            writer.testFinished(test1, TestResult.success(0))
            writer.testStarted(test4)
            writer.testStarted(test5)
            writer.testFinished(test5, TestResult.success(0))
            writer.testFinished(test4, TestResult.ignored("don't like it"))
            writer.specFinished(this@BasicConsoleWriterTest::class, null, emptyMap())
            writer.engineFinished(emptyList())
         }

         //println(out)

         out.shouldContainInOrder(
            "\tfirst test",
            "\tsecond test",
            "\t\tthird test",
            "fourth test",
            "\tfifth test"
         )
      }
   }

}

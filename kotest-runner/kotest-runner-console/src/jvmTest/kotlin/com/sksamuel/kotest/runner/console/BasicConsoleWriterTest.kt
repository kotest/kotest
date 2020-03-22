package com.sksamuel.kotest.runner.console

import io.kotest.core.spec.description
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.runner.console.BasicConsoleWriter
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class BasicConsoleWriterTest : FunSpec() {

   init {

      val desc = BasicConsoleWriterTest::class.description()

      test("final write should include summary info") {

         val test1 = TestCase.test(desc.append("a test"), this@BasicConsoleWriterTest) {}
         val test2 = TestCase.test(desc.append("b test"), this@BasicConsoleWriterTest) {}
         val test3 = TestCase.test(desc.append("c test"), this@BasicConsoleWriterTest) {}
         val test4 = TestCase.test(desc.append("d test"), this@BasicConsoleWriterTest) {}
         val test5 = TestCase.test(desc.append("e test"), this@BasicConsoleWriterTest) {}

         val out = captureStandardOut {
            val writer = BasicConsoleWriter()
            writer.engineStarted(emptyList())
            writer.specStarted(this@BasicConsoleWriterTest::class)
            writer.testStarted(test1)
            writer.testFinished(test1, TestResult.success(Duration.ZERO))
            writer.testStarted(test2)
            writer.testFinished(test2, TestResult.throwable(RuntimeException("wibble boom"), Duration.ZERO))
            writer.testStarted(test3)
            writer.testFinished(test3, TestResult.throwable(AssertionError("wobble vablam"), Duration.ZERO))
            writer.testStarted(test4)
            writer.testFinished(test4, TestResult.ignored("don't like it"))
            writer.testStarted(test5)
            writer.testFinished(test5, TestResult.success(Duration.ZERO))
            writer.specFinished(this@BasicConsoleWriterTest::class, null, emptyMap())
            writer.engineFinished(null)
         }

         //println(out)

         out.shouldContain("com.sksamuel.kotest.runner.console.BasicConsoleWriterTest")
         out.shouldContain("\ta test")
         out.shouldContain("\tcause: wibble boom (BasicConsoleWriterTest.kt:24)")
         out.shouldContain("Kotest completed in")
         out.shouldContain("Specs: completed 1, tests 5")
         out.shouldContain("Tests: passed 2, failed 2, ignored 1")
         out.shouldContain("*** 2 TESTS FAILED ***")
         out.shouldContain("Specs with failing tests:")
         out.shouldContain(" - com.sksamuel.kotest.runner.console.BasicConsoleWriterTest")

      }

      test("tests should be outputted in nested order") {

         val test1 =
            TestCase.container(desc.append("first test"), this@BasicConsoleWriterTest) {}

         val test2 =
            TestCase.container(
               desc.append("first test").append("second test"),
               this@BasicConsoleWriterTest
            ) {}

         val test3 = TestCase.test(
            desc.append("first test").append("second test").append("third test"),
            this@BasicConsoleWriterTest
         ) {}

         val test4 =
            TestCase.container(desc.append("fourth test"), this@BasicConsoleWriterTest) {}

         val test5 = TestCase.test(
            desc.append("fourth test").append("fifth test"),
            this@BasicConsoleWriterTest
         ) {}

         val out = captureStandardOut {
            val writer = BasicConsoleWriter()
            writer.engineStarted(emptyList())
            writer.specStarted(this@BasicConsoleWriterTest::class)
            writer.testStarted(test1)
            writer.testStarted(test2)
            writer.testStarted(test3)
            writer.testFinished(test3, TestResult.throwable(AssertionError("wobble vablam"), Duration.ZERO))
            writer.testFinished(test2, TestResult.throwable(RuntimeException("wibble boom"), Duration.ZERO))
            writer.testFinished(test1, TestResult.success(Duration.ZERO))
            writer.testStarted(test4)
            writer.testStarted(test5)
            writer.testFinished(test5, TestResult.success(Duration.ZERO))
            writer.testFinished(test4, TestResult.ignored("don't like it"))
            writer.specFinished(this@BasicConsoleWriterTest::class, null, emptyMap())
            writer.engineFinished(null)
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

package com.sksamuel.kotest.runner.console

import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.runner.console.BasicConsoleWriter
import io.kotest.specs.FunSpec
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
class BasicConsoleWriterTest : FunSpec() {

  init {

    test("final write should include summary info") {

      val test1 = TestCase.test(description().append("a test"), this@BasicConsoleWriterTest) {}
      val test2 = TestCase.test(description().append("b test"), this@BasicConsoleWriterTest) {}
      val test3 = TestCase.test(description().append("c test"), this@BasicConsoleWriterTest) {}
      val test4 = TestCase.test(description().append("d test"), this@BasicConsoleWriterTest) {}
      val test5 = TestCase.test(description().append("e test"), this@BasicConsoleWriterTest) {}

      val out = captureStandardOut {
        val writer = BasicConsoleWriter()
        writer.engineStarted(emptyList())
        writer.beforeSpecClass(this@BasicConsoleWriterTest::class)
        writer.enterTestCase(test1)
        writer.exitTestCase(test1, TestResult.success(Duration.ZERO))
        writer.enterTestCase(test2)
        writer.exitTestCase(test2, TestResult.error(RuntimeException("wibble boom"), Duration.ZERO))
        writer.enterTestCase(test3)
        writer.exitTestCase(test3, TestResult.failure(AssertionError("wobble vablam"), Duration.ZERO))
        writer.enterTestCase(test4)
        writer.exitTestCase(test4, TestResult.ignored("don't like it"))
        writer.enterTestCase(test5)
        writer.exitTestCase(test5, TestResult.success(Duration.ZERO))
        writer.afterSpecClass(this@BasicConsoleWriterTest::class, null)
        writer.engineFinished(null)
      }

      println(out)

      out.shouldContain("com.sksamuel.kotest.runner.console.DefaultConsoleWriterTest")
      out.shouldContain("\ta test")
      out.shouldContain("\t\tcause: wibble boom (DefaultConsoleWriterTest.kt:18)")
      out.shouldContain("Kotest completed in")
      out.shouldContain("1 spec containing 5 tests")
      out.shouldContain("Tests: passed 2, failed 2, ignored 1")
      out.shouldContain("*** 2 TESTS FAILED ***")
      out.shouldContain("Specs with failing tests:")
      out.shouldContain(" - com.sksamuel.kotest.runner.console.DefaultConsoleWriterTest")

    }

    test("tests should be outputted in nested order") {

      val test1 = TestCase.container(description().append("first test"), this@BasicConsoleWriterTest) {}
      val test2 = TestCase.container(description().append("first test").append("second test"),
          this@BasicConsoleWriterTest) {}
      val test3 = TestCase.test(description().append("first test").append("second test").append("third test"),
          this@BasicConsoleWriterTest) {}
      val test4 = TestCase.container(description().append("fourth test"), this@BasicConsoleWriterTest) {}
      val test5 = TestCase.test(description().append("fourth test").append("fifth test"),
          this@BasicConsoleWriterTest) {}

      val out = captureStandardOut {
        val writer = BasicConsoleWriter()
        writer.engineStarted(emptyList())
        writer.beforeSpecClass(this@BasicConsoleWriterTest::class)
        writer.enterTestCase(test1)
        writer.enterTestCase(test2)
        writer.enterTestCase(test3)
        writer.exitTestCase(test3, TestResult.failure(AssertionError("wobble vablam"), Duration.ZERO))
        writer.exitTestCase(test2, TestResult.error(RuntimeException("wibble boom"), Duration.ZERO))
        writer.exitTestCase(test1, TestResult.success(Duration.ZERO))
        writer.enterTestCase(test4)
        writer.enterTestCase(test5)
        writer.exitTestCase(test5, TestResult.success(Duration.ZERO))
        writer.exitTestCase(test4, TestResult.ignored("don't like it"))
        writer.afterSpecClass(this@BasicConsoleWriterTest::class, null)
        writer.engineFinished(null)
      }

      println(out)

      out.shouldContainInOrder(
          "\tfirst test",
          "\t\tsecond test",
          "\t\t\tthird test",
          "\t\tfourth test",
          "\tfifth test"
      )
    }
  }

}

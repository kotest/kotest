package com.sksamuel.kotlintest.runner.console

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.system.captureStandardOut
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldContainInOrder
import io.kotlintest.runner.console.DefaultConsoleWriter
import io.kotlintest.specs.FunSpec

class DefaultConsoleWriterTest : FunSpec() {

  init {

    test("final write should include summary info") {

      val test1 = TestCase.test(description().append("a test"), this@DefaultConsoleWriterTest) {}
      val test2 = TestCase.test(description().append("b test"), this@DefaultConsoleWriterTest) {}
      val test3 = TestCase.test(description().append("c test"), this@DefaultConsoleWriterTest) {}
      val test4 = TestCase.test(description().append("d test"), this@DefaultConsoleWriterTest) {}
      val test5 = TestCase.test(description().append("e test"), this@DefaultConsoleWriterTest) {}

      val out = captureStandardOut {
        val writer = DefaultConsoleWriter()
        writer.engineStarted(emptyList())
        writer.beforeSpecClass(this@DefaultConsoleWriterTest::class)
        writer.enterTestCase(test1)
        writer.exitTestCase(test1, TestResult.Success)
        writer.enterTestCase(test2)
        writer.exitTestCase(test2, TestResult.error(RuntimeException("wibble boom")))
        writer.enterTestCase(test3)
        writer.exitTestCase(test3, TestResult.failure(AssertionError("wobble vablam")))
        writer.enterTestCase(test4)
        writer.exitTestCase(test4, TestResult.ignored("don't like it"))
        writer.enterTestCase(test5)
        writer.exitTestCase(test5, TestResult.Success)
        writer.afterSpecClass(this@DefaultConsoleWriterTest::class, null)
        writer.engineFinished(null)
      }

      println(out)

      out.shouldContain("com.sksamuel.kotlintest.runner.console.DefaultConsoleWriterTest")
      out.shouldContain("\ta test")
      out.shouldContain("\t\tcause: wibble boom (DefaultConsoleWriterTest.kt:18)")
      out.shouldContain("KotlinTest completed in")
      out.shouldContain("1 spec containing 5 tests")
      out.shouldContain("Tests: passed 2, failed 2, ignored 1")
      out.shouldContain("*** 2 TESTS FAILED ***")
      out.shouldContain("Specs with failing tests:")
      out.shouldContain(" - com.sksamuel.kotlintest.runner.console.DefaultConsoleWriterTest")

    }

    test("tests should be outputted in nested order") {

      val test1 = TestCase.container(description().append("first test"), this@DefaultConsoleWriterTest) {}
      val test2 = TestCase.container(description().append("first test").append("second test"),
          this@DefaultConsoleWriterTest) {}
      val test3 = TestCase.test(description().append("first test").append("second test").append("third test"),
          this@DefaultConsoleWriterTest) {}
      val test4 = TestCase.container(description().append("fourth test"), this@DefaultConsoleWriterTest) {}
      val test5 = TestCase.test(description().append("fourth test").append("fifth test"),
          this@DefaultConsoleWriterTest) {}

      val out = captureStandardOut {
        val writer = DefaultConsoleWriter()
        writer.engineStarted(emptyList())
        writer.beforeSpecClass(this@DefaultConsoleWriterTest::class)
        writer.enterTestCase(test1)
        writer.enterTestCase(test2)
        writer.enterTestCase(test3)
        writer.exitTestCase(test3, TestResult.failure(AssertionError("wobble vablam")))
        writer.exitTestCase(test2, TestResult.error(RuntimeException("wibble boom")))
        writer.exitTestCase(test1, TestResult.Success)
        writer.enterTestCase(test4)
        writer.enterTestCase(test5)
        writer.exitTestCase(test5, TestResult.Success)
        writer.exitTestCase(test4, TestResult.ignored("don't like it"))
        writer.afterSpecClass(this@DefaultConsoleWriterTest::class, null)
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
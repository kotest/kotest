package com.sksamuel.kotlintest.runner.console

import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.TestType
import io.kotlintest.extensions.system.captureStandardOut
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.runner.console.DefaultConsoleWriter
import io.kotlintest.specs.FunSpec

class DefaultConsoleWriterTest : FunSpec() {

  private val test1 = TestCase(description().append("first test"), this@DefaultConsoleWriterTest, {},
      1, TestType.Test, TestCaseConfig(enabled = true))
  private val test2 = TestCase(description().append("second test"), this@DefaultConsoleWriterTest, {},
      1, TestType.Test, TestCaseConfig(enabled = false))
  private val test3 = TestCase(description().append("third test"), this@DefaultConsoleWriterTest, {},
      1, TestType.Test, TestCaseConfig(enabled = false))
  private val test4 = TestCase(description().append("fourth test"), this@DefaultConsoleWriterTest, {},
      1, TestType.Test, TestCaseConfig(enabled = false))
  private val test5 = TestCase(description().append("fifth test"), this@DefaultConsoleWriterTest, {},
      1, TestType.Test, TestCaseConfig(enabled = false))

  init {

    test("final write should include summary info") {

      val out = captureStandardOut {
        val writer = DefaultConsoleWriter()
        writer.engineStarted(emptyList())
        writer.beforeSpecClass(this@DefaultConsoleWriterTest::class)
        writer.exitTestCase(test1, TestResult.Success)
        writer.exitTestCase(test2, TestResult.error(RuntimeException("wibble boom")))
        writer.exitTestCase(test3, TestResult.failure(AssertionError("wobble vablam")))
        writer.exitTestCase(test4, TestResult.ignored("don't like it"))
        writer.exitTestCase(test5, TestResult.Success)
        writer.engineFinished(null)
      }

      println(out)

      out.shouldContain("com.sksamuel.kotlintest.runner.console.DefaultConsoleWriterTest")
      out.shouldContain("\tfirst test")
      out.shouldContain("\t\tcause: wibble boom (sourcefile.kt 1)")
      out.shouldContain("KotlinTest completed in")
      out.shouldContain("1 spec containing 5 tests")
      out.shouldContain("Tests: passed 2, failed 2, ignored 1")
      out.shouldContain("*** 2 TESTS FAILED ***")
      out.shouldContain("Specs with failing tests:")
      out.shouldContain(" - com.sksamuel.kotlintest.runner.console.DefaultConsoleWriterTest")

    }
  }

}
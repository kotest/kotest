package com.sksamuel.kotest.engine.extensions.test.testextension

import io.kotest.core.annotation.Description
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

private var string = ""

class TestCaseExtensionAppender(private val char: Char) : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      string += char
      val result = execute(testCase)
      return result
   }
}

@Description("Tests the specification of precedence for TestCaseExtensions")
@EnabledIf(LinuxCondition::class)
class TestCaseExtensionPrecedenceTest : FunSpec() {

   override val extensions = listOf(TestCaseExtensionAppender('a'), TestCaseExtensionAppender('b'))

   init {

      extensions(TestCaseExtensionAppender('c'), TestCaseExtensionAppender('d'))

      test("precedence specification").config(extensions = listOf(TestCaseExtensionAppender('e'))) {
         // closest TestCaseExtensions should run last so they can override project/spec level TestCaseExtensions
         string shouldBe "dcbae"
      }
   }
}

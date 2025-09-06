package com.sksamuel.kotest.engine.callback.order

import io.kotest.core.annotation.Description
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe

private var string = ""

class TestCaseExtensionAppender(private val char: Char) : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      string += char
      val result = execute(testCase)
      return result
   }

   override fun toString(): String {
      return "test extension $char"
   }
}

@Description("Tests the specification of precedence for TestCaseExtensions")
@EnabledIf(LinuxOnlyGithubCondition::class)
class TestCaseExtensionPrecedenceTest : FunSpec() {

   override val extensions = listOf(TestCaseExtensionAppender('a'), TestCaseExtensionAppender('b'))

   init {

      extensions(TestCaseExtensionAppender('c'), TestCaseExtensionAppender('d'))

      test("precedence specification").config(extensions = listOf(TestCaseExtensionAppender('e'))) {
         // closest TestCaseExtensions should run last so they can override project/spec level TestCaseExtensions
         string shouldBe "abcde"
      }
   }
}

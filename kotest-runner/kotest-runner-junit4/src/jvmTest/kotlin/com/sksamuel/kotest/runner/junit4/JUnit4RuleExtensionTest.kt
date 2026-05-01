package com.sksamuel.kotest.runner.junit4

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.runner.junit4.JUnit4RuleExtension
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class JUnit4RuleExtensionTest : FunSpec({

   test("rule that skips by not calling base.evaluate() should produce TestResult.Ignored without running the test") {
      val spec = SkippingRuleSpec()
      val testCase = TestCase(
         descriptor = SkippingRuleSpec::class.toDescriptor().append("foo"),
         name = TestNameBuilder.builder("foo").build(),
         spec = spec,
         test = {},
         source = SourceRef.None,
         type = TestType.Test,
      )

      var executed = false
      val result = runBlocking {
         JUnit4RuleExtension.intercept(testCase) {
            executed = true
            TestResultBuilder.builder().build()
         }
      }

      executed shouldBe false
      result.shouldBeInstanceOf<TestResult.Ignored>()
   }

   test("rule that invokes base.evaluate() should run the test and return its result") {
      val spec = PassThroughRuleSpec()
      val testCase = TestCase(
         descriptor = PassThroughRuleSpec::class.toDescriptor().append("foo"),
         name = TestNameBuilder.builder("foo").build(),
         spec = spec,
         test = {},
         source = SourceRef.None,
         type = TestType.Test,
      )

      var executed = false
      val expected = TestResultBuilder.builder().build()
      val result = runBlocking {
         JUnit4RuleExtension.intercept(testCase) {
            executed = true
            expected
         }
      }

      executed shouldBe true
      result shouldBe expected
   }
})

private class SkippingRuleSpec : FunSpec() {
   @get:Rule
   val rule: TestRule = TestRule { _, _ ->
      object : Statement() {
         override fun evaluate() {
            // Intentionally do not call base.evaluate() — emulating a ConditionalIgnoreRule.
         }
      }
   }
}

private class PassThroughRuleSpec : FunSpec() {
   @get:Rule
   val rule: TestRule = TestRule { base, _ ->
      object : Statement() {
         override fun evaluate() {
            base.evaluate()
         }
      }
   }
}

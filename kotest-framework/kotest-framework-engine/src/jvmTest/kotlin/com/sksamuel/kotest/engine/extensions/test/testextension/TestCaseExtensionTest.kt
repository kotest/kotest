package com.sksamuel.kotest.engine.extensions.test.testextension

import io.kotest.core.annotation.Description
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private object Numbers {
   val a = AtomicInteger(0)
   val b = AtomicInteger(0)
}

object TestCaseExtensionAdder : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      Numbers.a.incrementAndGet()
      val result = execute(testCase)
      Numbers.b.incrementAndGet()
      return result
   }
}

@Description("tests that we can use around advice with intercept")
@EnabledIf(LinuxCondition::class)
class TestCaseExtensionTest : FunSpec() {

   override val extensions = listOf(TestCaseExtensionAdder)

   init {

      extension(TestCaseExtensionAdder)

      test("be activated by registration with ProjectExtensions") {
         // the interceptor should have incremented a but not b since b is coming after the test executes
         Numbers.a.get() shouldBe 2
         Numbers.b.get() shouldBe 0
      }
      test("use around advice") {
         // in this second test, both the after from the previous test, and the before of this test should have fired
         Numbers.a.get() shouldBe 4
         Numbers.b.get() shouldBe 2
      }
      test("use extensions registered on config").config(extensions = listOf(TestCaseExtensionAdder)) {
         // in this third test, the before from the previous test plus the new interceptor, should have fired
         // so it is double adding to a
         Numbers.a.get() shouldBe 7
         Numbers.b.get() shouldBe 4
      }
   }
}


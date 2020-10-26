package com.sksamuel.kotest.engine.launcher

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.launcher.execute
import io.kotest.engine.reporter.Reporter
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.reflect.KClass

class ExecuteTest : FunSpec() {
   init {
      test("execute should report throwables during engine setup") {

         var errors = emptyList<Throwable>()

         val reporter = object : Reporter {

            override fun hasErrors(): Boolean {
               return true
            }

            override fun engineStarted(classes: List<KClass<out Spec>>) {}

            override fun engineFinished(t: List<Throwable>) {
               errors = t
            }

            override fun specStarted(kclass: KClass<out Spec>) {}

            override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {}

            override fun testStarted(testCase: TestCase) {}

            override fun testFinished(testCase: TestCase, result: TestResult) {}
         }

         execute(
            reporter,
            null,
            "unknown.class",
            null,
            null
         )

         errors.shouldHaveSize(1)
      }
   }
}

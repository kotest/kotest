package com.sksamuel.kotest.runner.junit5

import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.toTestResult
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.JUnitTestEngineListener
import io.kotest.runner.junit.platform.KotestEngineDescriptor
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.reporting.ReportEntry
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class JUnitTestRunnerListenerTests : FunSpec({

   test("a bad test should fail test but not parent or spec") {

      val root = KotestEngineDescriptor(
         UniqueId.forEngine("kotest"),
         emptyList(),
         emptyList()
      )

      val finished = mutableMapOf<String, TestExecutionResult.Status>()

      val engineListener = object : EngineExecutionListener {
         override fun executionFinished(testDescriptor: TestDescriptor, testExecutionResult: TestExecutionResult) {
            finished[testDescriptor.displayName] = testExecutionResult.status
         }

         override fun reportingEntryPublished(testDescriptor: TestDescriptor?, entry: ReportEntry?) {}
         override fun executionSkipped(testDescriptor: TestDescriptor?, reason: String?) {}
         override fun executionStarted(testDescriptor: TestDescriptor?) {}
         override fun dynamicTestRegistered(testDescriptor: TestDescriptor?) {}
      }

      val test1 = TestCase(
         JUnitTestRunnerListenerTests::class.toDescription().appendTest("test1"),
         JUnitTestRunnerListenerTests(),
         { },
         sourceRef(),
         TestType.Container
      )

      val test2 = TestCase(
         test1.description.appendTest("test2"),
         JUnitTestRunnerListenerTests(),
         { },
         sourceRef(),
         TestType.Container
      )

      val listener = JUnitTestEngineListener(engineListener, root)
      listener.engineStarted(emptyList())
      listener.specStarted(JUnitTestRunnerListenerTests::class)
      listener.testStarted(test1)
      listener.testStarted(test2)
      listener.testFinished(test2, toTestResult(AssertionError("boom"), 0))
      listener.testFinished(test1, TestResult.success(0))
      listener.specFinished(JUnitTestRunnerListenerTests::class, null, emptyMap())
      listener.engineFinished(emptyList())

      finished.toMap() shouldBe mapOf(
         "test2" to TestExecutionResult.Status.FAILED,
         "test1" to TestExecutionResult.Status.SUCCESSFUL,
         "com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTests" to TestExecutionResult.Status.SUCCESSFUL,
         "Kotest" to TestExecutionResult.Status.SUCCESSFUL
      )
   }

})

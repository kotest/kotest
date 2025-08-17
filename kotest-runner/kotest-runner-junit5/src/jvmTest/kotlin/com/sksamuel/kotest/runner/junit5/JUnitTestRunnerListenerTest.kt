package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.descriptors.toDescriptor
import io.kotest.engine.test.TestResultBuilder
import io.kotest.engine.test.names.DisplayNameFormatting
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.JUnitTestEngineListener
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import io.kotest.runner.junit.platform.createEngineDescriptor
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.reporting.ReportEntry
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class JUnitTestRunnerListenerTest : DescribeSpec({

   describe("as per the JUnit spec") {
      it("a failing test should not fail the parent test or parent spec") {

         val root = createEngineDescriptor(
            UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID),
            listOf(JUnitTestRunnerListenerTest::class),
            emptyList(),
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
            JUnitTestRunnerListenerTest::class.toDescriptor().append("test1"),
            TestNameBuilder.builder("test1").build(),
            JUnitTestRunnerListenerTest(),
            { },
            SourceRef.None,
            TestType.Container,
            parent = null,
         )

         val test2 = TestCase(
            test1.descriptor.append("test2"),
            TestNameBuilder.builder("test2").build(),
            JUnitTestRunnerListenerTest(),
            { },
            SourceRef.None,
            TestType.Test,
            parent = test1,
         )

         val listener = JUnitTestEngineListener(engineListener, root, DisplayNameFormatting(null))
         listener.engineStarted()
         listener.specStarted(SpecRef.Reference(JUnitTestRunnerListenerTest::class))
         listener.specStarted(SpecRef.Reference(JUnitTestRunnerListenerTest::class))
         listener.testStarted(test1)
         listener.testStarted(test2)
         listener.testFinished(test2, TestResultBuilder.builder().withError(AssertionError("boom")).build())
         listener.testFinished(test1, TestResult.Success(0.milliseconds))
         listener.specFinished(SpecRef.Reference(JUnitTestRunnerListenerTest::class), TestResult.Success(0.seconds))
         listener.engineFinished(emptyList())

         finished.toMap() shouldBe mapOf(
            "test2" to TestExecutionResult.Status.FAILED,
            "test1" to TestExecutionResult.Status.SUCCESSFUL,
            "com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest" to TestExecutionResult.Status.SUCCESSFUL,
            KotestJunitPlatformTestEngine.ENGINE_NAME to TestExecutionResult.Status.SUCCESSFUL
         )
      }
   }
})

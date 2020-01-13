package com.sksamuel.kotest.runner.junit5

import io.kotest.core.*
import io.kotest.core.spec.CompositeSpec
import io.kotest.core.spec.description
import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.runner.junit5.JUnitTestEngineListener
import io.kotest.runner.junit5.KotestEngineDescriptor
import io.kotest.shouldBe
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.reporting.ReportEntry
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
val childFailsParentTest = funSpec {

    test("failed test should fail parent and spec") {

        val root = KotestEngineDescriptor(UniqueId.forEngine("kotest"), emptyList())

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
            JUnitTestRunnerListenerTests::class.description().append("test1"),
            JUnitTestRunnerListenerTests(),
            { },
            sourceRef(),
            TestType.Container
        )

        val test2 = TestCase(
            test1.description.append("test2"),
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
        listener.testFinished(test2, TestResult.failure(AssertionError("boom"), Duration.ZERO))
        listener.testFinished(test1, TestResult.success(Duration.ZERO))
        listener.specFinished(JUnitTestRunnerListenerTests::class, null, emptyMap())
        listener.engineFinished(null)

        finished.toMap() shouldBe mapOf(
            "test1" to TestExecutionResult.Status.FAILED,
            "test2" to TestExecutionResult.Status.FAILED,
            "com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTests" to TestExecutionResult.Status.FAILED,
            "Kotest" to TestExecutionResult.Status.SUCCESSFUL
        )
    }
}

class JUnitTestRunnerListenerTests : CompositeSpec(childFailsParentTest)

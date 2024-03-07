package com.sksamuel.kotest.runner.junit5

import io.kotest.common.Platform
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.testSpecExecutor
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.JUnitTestEngineListener
import io.kotest.runner.junit.platform.KotestEngineDescriptor
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.reporting.ReportEntry

class SpecInitializationErrorTest : FunSpec({

   test("an error in a class field should fail spec") {

      val root = KotestEngineDescriptor(
         UniqueId.forEngine("kotest"),
         ProjectConfiguration(),
         emptyList(),
         emptyList(),
         emptyList(),
         null,
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

      val listener = JUnitTestEngineListener(engineListener, root, FallbackDisplayNameFormatter.default())
      testSpecExecutor(
         NoopCoroutineDispatcherFactory,
         EngineContext(ProjectConfiguration(), Platform.JVM).mergeListener(listener),
         SpecRef.Reference(SpecWithFieldError::class)
      )

      finished.toMap() shouldBe mapOf(
         "SpecInstantiationException" to TestExecutionResult.Status.FAILED,
         "com.sksamuel.kotest.runner.junit5.SpecWithFieldError" to TestExecutionResult.Status.FAILED
      )
   }

   test("an error in a class initializer should fail spec") {

      val root = KotestEngineDescriptor(
         UniqueId.forEngine("kotest"),
         ProjectConfiguration(),
         emptyList(),
         emptyList(),
         emptyList(),
         null,
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

      val listener = JUnitTestEngineListener(engineListener, root, FallbackDisplayNameFormatter.default())
      testSpecExecutor(
         NoopCoroutineDispatcherFactory,
         EngineContext(ProjectConfiguration(), Platform.JVM).mergeListener(listener),
         SpecRef.Reference(SpecWithInitError::class)
      )

      finished.toMap() shouldBe mapOf(
         "SpecInstantiationException" to TestExecutionResult.Status.FAILED,
         "com.sksamuel.kotest.runner.junit5.SpecWithInitError" to TestExecutionResult.Status.FAILED,
      )
   }
})

private class SpecWithFieldError : FunSpec() {
   private val err = "failme".apply { error("foo") }

   init {
      test("foo") {
      }
   }
}

private class SpecWithInitError : FunSpec() {
   init {
      error("foo")
      test("foo") {
      }
   }
}

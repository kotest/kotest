package com.sksamuel.kotest.runner.junit5

import io.kotest.common.Platform
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.execution.testSpecExecutor
import io.kotest.engine.test.names.DisplayNameFormatting
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.EngineDescriptorBuilder
import io.kotest.runner.junit.platform.JUnitTestEngineListener
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId

@EnabledIf(LinuxOnlyGithubCondition::class)
class SpecInitializationErrorTest : FunSpec({

   test("an error in a class field should fail spec") {

      val root = EngineDescriptorBuilder
         .builder(UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID))
         .withSpecs(listOf(SpecWithInstanceFieldError::class))
         .build()

      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))

      testSpecExecutor(
         EngineContext(null, Platform.JVM).mergeListener(listener),
         SpecRef.Reference(SpecWithInstanceFieldError::class)
      )

      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("SpecWithInstanceFieldError"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "SpecInstantiationException",
            "com.sksamuel.kotest.runner.junit5.SpecWithInstanceFieldError",
            "SpecInstantiationException"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("SpecInstantiationException"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "SpecInstantiationException",
            TestExecutionResult.Status.FAILED
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "SpecWithInstanceFieldError",
            TestExecutionResult.Status.FAILED,
         ),
      )
   }

   test("an error in a class initializer should fail spec") {

      val root = EngineDescriptorBuilder
         .builder(UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID))
         .withSpecs(listOf(SpecWithInitError::class))
         .build()

      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))

      testSpecExecutor(
         EngineContext(null, Platform.JVM).mergeListener(listener),
         SpecRef.Reference(SpecWithInitError::class)
      )

      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("SpecWithInitError"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "SpecInstantiationException",
            "com.sksamuel.kotest.runner.junit5.SpecWithInitError",
            "SpecInstantiationException"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("SpecInstantiationException"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "SpecInstantiationException",
            TestExecutionResult.Status.FAILED
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "SpecWithInitError",
            TestExecutionResult.Status.FAILED,
         ),
      )
   }
})

private class SpecWithInstanceFieldError : FunSpec() {
   @Suppress("unused")
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

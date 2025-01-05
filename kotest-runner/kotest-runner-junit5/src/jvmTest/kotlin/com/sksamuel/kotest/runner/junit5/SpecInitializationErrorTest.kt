package com.sksamuel.kotest.runner.junit5

import io.kotest.core.Platform
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.testSpecExecutor
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.JUnitTestEngineListener
import io.kotest.runner.junit.platform.createEngineDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId

@EnabledIf(LinuxCondition::class)
class SpecInitializationErrorTest : FunSpec({

   test("an error in a class field should fail spec") {

      val root = createEngineDescriptor(
         UniqueId.forEngine("kotest"),
         ProjectConfiguration(),
         listOf(SpecWithInstanceFieldError::class),
         null,
         null,
      )

      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, FallbackDisplayNameFormatter.default())

      testSpecExecutor(
         EngineContext(ProjectConfiguration(), Platform.JVM).mergeListener(listener),
         SpecRef.Reference(SpecWithInstanceFieldError::class)
      )

      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.SpecWithInstanceFieldError"),
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
            "com.sksamuel.kotest.runner.junit5.SpecWithInstanceFieldError",
            TestExecutionResult.Status.FAILED,
         ),
      )
   }

   test("an error in a class initializer should fail spec") {

      val root = createEngineDescriptor(
         UniqueId.forEngine("kotest"),
         ProjectConfiguration(),
         listOf(SpecWithInitError::class),
         null,
         null,
      )

      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, FallbackDisplayNameFormatter.default())

      testSpecExecutor(
         EngineContext(ProjectConfiguration(), Platform.JVM).mergeListener(listener),
         SpecRef.Reference(SpecWithInitError::class)
      )

      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.SpecWithInitError"),
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
            "com.sksamuel.kotest.runner.junit5.SpecWithInitError",
            TestExecutionResult.Status.FAILED,
         ),
      )
   }
})

private class SpecWithInstanceFieldError : FunSpec() {
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

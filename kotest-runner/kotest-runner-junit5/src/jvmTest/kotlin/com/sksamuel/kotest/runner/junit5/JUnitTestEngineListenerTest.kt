package com.sksamuel.kotest.runner.junit5

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.JUnitTestEngineListener
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.engine.support.descriptor.EngineDescriptor

class JUnitTestEngineListenerTest : FunSpec({

   val root = EngineDescriptor(UniqueId.forEngine("kotest"), "kotest")

   val tc1 = TestCase(
      MySpec::class.toDescriptor().append("foo"),
      TestName("foo"),
      MySpec(),
      {},
      sourceRef(),
      TestType.Container,
   )

   val tc2 = TestCase(
      MySpec::class.toDescriptor().append("foo").append("bar"),
      TestName("bar"),
      MySpec(),
      {},
      sourceRef(),
      TestType.Test,
      parent = tc1,
   )

   test("an error before spec started should show spec with a dummy error test") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specExit(MySpec::class, Exception("CRRAACK"))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("<error>", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("<error>"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("<error>", TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("MySpec", TestExecutionResult.Status.FAILED),
      )
   }

   test("an ignored spec should generate no events") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specIgnored(MySpec::class)
      listener.specExit(MySpec::class, null)
      track.events.shouldBeEmpty()
   }

   test("an inactive spec should be marked as skipped") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specInactive(
         MySpec::class,
         mapOf(tc1 to TestResult.ignored(null), tc2 to TestResult.ignored(null))
      )
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("MySpec"),
      )
   }

   test("a successful root test should be marked as started and finished") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.success(12))
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("a failed root test should be marked as FAILED") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.failure(AssertionError("whack!"), 5))
      listener.specFinished(MySpec::class, mapOf(tc1 to TestResult.failure(AssertionError("whack!"), 5)))
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("an ignored root test should be marked as skipped") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testIgnored(tc1, "secret!")
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("a successful nested test should be marked as SUCCESSFUL with type TEST") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testStarted(tc2)
      listener.testFinished(tc2, TestResult.success(3))
      listener.testFinished(tc1, TestResult.success(7))
      listener.specFinished(
         MySpec::class,
         mapOf(tc1 to TestResult.success(7), tc2 to TestResult.ignored("secret!"))
      )
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER_AND_TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("bar", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("bar"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("bar", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("a failed nested test should be marked as FAILED with type TEST") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testStarted(tc2)
      listener.testFinished(tc2, TestResult.failure(AssertionError("whack!"), 5))
      listener.testFinished(tc1, TestResult.success(7))
      listener.specFinished(
         MySpec::class,
         mapOf(tc1 to TestResult.success(7), tc2 to TestResult.ignored("secret!"))
      )
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER_AND_TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("bar", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("bar"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("bar", TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("an ignored nested test should be marked as skipped") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testIgnored(tc2, "secret!")
      listener.testFinished(tc1, TestResult.success(7))
      listener.specFinished(
         MySpec::class,
         mapOf(tc1 to TestResult.success(7), tc2 to TestResult.ignored("secret!"))
      )
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER_AND_TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("bar", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("bar"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("an error in the spec should add a placeholder test with the error") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.success(7))
      listener.specFinished(
         MySpec::class,
         mapOf(tc1 to TestResult.success(7))
      )
      listener.specExit(MySpec::class, Exception("THWAPP!"))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("<error>", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("<error>"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("<error>", TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("MySpec", TestExecutionResult.Status.FAILED),
      )
   }
})

private class MySpec : FunSpec() {}

class EventTrackingEngineExecutionListener : EngineExecutionListener {

   sealed interface Event {
      data class TestRegistered(val descriptor: String, val type: TestDescriptor.Type) : Event
      data class ExecutionSkipped(val descriptor: String) : Event
      data class ExecutionStarted(val descriptor: String) : Event
      data class ExecutionFinished(val descriptor: String, val status: TestExecutionResult.Status) : Event
   }

   val events = mutableListOf<Event>()

   override fun dynamicTestRegistered(testDescriptor: TestDescriptor) {
      events.add(Event.TestRegistered(testDescriptor.displayName, testDescriptor.type))
   }

   override fun executionSkipped(testDescriptor: TestDescriptor, reason: String?) {
      events.add(Event.ExecutionSkipped(testDescriptor.displayName))
   }

   override fun executionStarted(testDescriptor: TestDescriptor) {
      events.add(Event.ExecutionStarted(testDescriptor.displayName))
   }

   override fun executionFinished(testDescriptor: TestDescriptor, testExecutionResult: TestExecutionResult) {
      events.add(Event.ExecutionFinished(testDescriptor.displayName, testExecutionResult.status))
   }

   override fun reportingEntryPublished(testDescriptor: TestDescriptor?, entry: ReportEntry?) {
      error("Unused")
   }
}

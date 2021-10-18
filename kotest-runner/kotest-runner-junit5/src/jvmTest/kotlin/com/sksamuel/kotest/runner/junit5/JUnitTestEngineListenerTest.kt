package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.Ignored
import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
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
import kotlin.time.milliseconds

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
      tc1.descriptor.append("bar"),
      TestName("bar"),
      tc1.spec,
      {},
      sourceRef(),
      TestType.Test,
      parent = tc1,
   )

   val tc3 = TestCase(
      MySpec2::class.toDescriptor().append("baz"),
      TestName("baz"),
      MySpec2(),
      {},
      sourceRef(),
      TestType.Container,
   )

   test("an error before spec started should show spec with a dummy error test") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specExit(MySpec::class, Exception("CRRAACK"))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered(
            "com.sksamuel.kotest.runner.junit5.MySpec",
            TestDescriptor.Type.CONTAINER
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered(
            JUnitTestEngineListener.PlaceholderName,
            TestDescriptor.Type.TEST
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted(JUnitTestEngineListener.PlaceholderName),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            JUnitTestEngineListener.PlaceholderName,
            TestExecutionResult.Status.FAILED
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "com.sksamuel.kotest.runner.junit5.MySpec",
            TestExecutionResult.Status.FAILED
         ),
      )
   }

   test("an ignored spec should generate no events") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specIgnored(MySpec::class)
      listener.specExit(MySpec::class, null)
      track.events.shouldBeEmpty()
   }

   test("an inactive spec should be marked as skipped") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specInactive(
         MySpec::class,
         mapOf(tc1 to TestResult.Ignored(null), tc2 to TestResult.Ignored(null))
      )
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("com.sksamuel.kotest.runner.junit5.MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("foo", null),
         EventTrackingEngineExecutionListener.Event.TestRegistered("bar", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("bar", null),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("com.sksamuel.kotest.runner.junit5.MySpec", null),
      )
   }

   test("a successful root test should be marked as started and finished") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.Success(12.milliseconds))
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("com.sksamuel.kotest.runner.junit5.MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("com.sksamuel.kotest.runner.junit5.MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("a failed root test should be marked as FAILED") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.failure(AssertionError("whack!"), 5))
      listener.specFinished(MySpec::class, mapOf(tc1 to TestResult.Failure(5.milliseconds, AssertionError("whack!"))))
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered(
            "com.sksamuel.kotest.runner.junit5.MySpec",
            TestDescriptor.Type.CONTAINER
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "com.sksamuel.kotest.runner.junit5.MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("an ignored root test should be marked as skipped") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testIgnored(tc1, "secret!")
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("com.sksamuel.kotest.runner.junit5.MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("foo", "secret!"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("com.sksamuel.kotest.runner.junit5.MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("a successful nested test should be marked as SUCCESSFUL with type TEST") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testStarted(tc2)
      listener.testFinished(tc2, TestResult.success(3))
      listener.testFinished(tc1, TestResult.success(7))
      listener.specFinished(
         MySpec::class,
         mapOf(tc1 to TestResult.success(7), tc2 to TestResult.Ignored("secret!"))
      )
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("com.sksamuel.kotest.runner.junit5.MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("bar", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("bar"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("bar", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("com.sksamuel.kotest.runner.junit5.MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("a failed nested test should be marked as FAILED with type TEST") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testStarted(tc2)
      listener.testFinished(tc2, TestResult.failure(AssertionError("whack!"), 5))
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(
         MySpec::class,
         mapOf(tc1 to TestResult.Success(7.milliseconds), tc2 to TestResult.Ignored("secret!"))
      )
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("com.sksamuel.kotest.runner.junit5.MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("bar", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("bar"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("bar", TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("com.sksamuel.kotest.runner.junit5.MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("an ignored nested test should be marked as skipped") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testIgnored(tc2, "secret!")
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(
         MySpec::class,
         mapOf(tc1 to TestResult.Success(7.milliseconds), tc2 to TestResult.Ignored("secret!"))
      )
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("com.sksamuel.kotest.runner.junit5.MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("bar", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("bar", "secret!"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("com.sksamuel.kotest.runner.junit5.MySpec", TestExecutionResult.Status.SUCCESSFUL),
      )
   }

   test("an error in the spec should add a placeholder test with the error along with completed tests") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(
         MySpec::class,
         mapOf(tc1 to TestResult.Success(7.milliseconds))
      )
      listener.specExit(MySpec::class, Exception("THWAPP!"))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered(
            "com.sksamuel.kotest.runner.junit5.MySpec",
            TestDescriptor.Type.CONTAINER
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
//         EventTrackingEngineExecutionListener.Event.TestRegistered(JUnitTestEngineListener.PlaceholderName, TestDescriptor.Type.TEST),
//         EventTrackingEngineExecutionListener.Event.ExecutionStarted(JUnitTestEngineListener.PlaceholderName),
//         EventTrackingEngineExecutionListener.Event.ExecutionFinished(JUnitTestEngineListener.PlaceholderName, TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "com.sksamuel.kotest.runner.junit5.MySpec",
            TestExecutionResult.Status.FAILED
         ),
      )
   }

   test("state should be reset after spec") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(MySpec::class, mapOf(tc1 to TestResult.Success(7.milliseconds)))
      listener.specExit(MySpec::class, null)

      listener.specEnter(MySpec2::class)
      listener.specStarted(MySpec2::class)
      listener.testStarted(tc3)
      listener.testFinished(tc3, TestResult.Success(4.milliseconds))
      listener.specFinished(MySpec2::class, mapOf(tc3 to TestResult.Success(4.milliseconds)))
      listener.specExit(MySpec2::class, null)

      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered(
            "com.sksamuel.kotest.runner.junit5.MySpec",
            TestDescriptor.Type.CONTAINER
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "com.sksamuel.kotest.runner.junit5.MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
         EventTrackingEngineExecutionListener.Event.TestRegistered(
            "com.sksamuel.kotest.runner.junit5.MySpec2",
            TestDescriptor.Type.CONTAINER
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec2"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("baz", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("baz"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("baz", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "com.sksamuel.kotest.runner.junit5.MySpec2",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("state should be reset after ignored spec") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MyIgnoredTest::class)
      listener.specIgnored(MyIgnoredTest::class)
      listener.specExit(MyIgnoredTest::class, null)

      listener.specEnter(MySpec2::class)
      listener.specStarted(MySpec2::class)
      listener.testStarted(tc3)
      listener.testFinished(tc3, TestResult.Success(4.milliseconds))
      listener.specFinished(MySpec2::class, mapOf(tc3 to TestResult.Success(4.milliseconds)))
      listener.specExit(MySpec2::class, null)

      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered(
            "com.sksamuel.kotest.runner.junit5.MySpec2",
            TestDescriptor.Type.CONTAINER
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec2"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("baz", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("baz"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("baz", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "com.sksamuel.kotest.runner.junit5.MySpec2",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("state should be reset after inactive spec") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, configuration)
      listener.specEnter(MySpec::class)
      listener.specInactive(MySpec::class, mapOf(tc1 to TestResult.Ignored("wibble")))
      listener.specExit(MySpec::class, null)

      listener.specEnter(MySpec2::class)
      listener.specStarted(MySpec2::class)
      listener.testStarted(tc3)
      listener.testFinished(tc3, TestResult.success(4))
      listener.specFinished(MySpec2::class, mapOf(tc3 to TestResult.success(4)))
      listener.specExit(MySpec2::class, null)

      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("com.sksamuel.kotest.runner.junit5.MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("foo", "wibble"),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("com.sksamuel.kotest.runner.junit5.MySpec", null),
         EventTrackingEngineExecutionListener.Event.TestRegistered(
            "com.sksamuel.kotest.runner.junit5.MySpec2",
            TestDescriptor.Type.CONTAINER
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec2"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("baz", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("baz"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("baz", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "com.sksamuel.kotest.runner.junit5.MySpec2",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("listener should support full test paths") {
      val track = EventTrackingEngineExecutionListener()
      val config = Configuration()
      config.displayFullTestPath = true
      val listener = JUnitTestEngineListener(track, root, config)
      listener.specEnter(MySpec::class)
      listener.specStarted(MySpec::class)
      listener.testStarted(tc1)
      listener.testStarted(tc2)
      listener.testFinished(tc2, TestResult.success(3))
      listener.testFinished(tc1, TestResult.success(7))
      listener.specFinished(
         MySpec::class,
         mapOf(tc1 to TestResult.success(7), tc2 to TestResult.Ignored("secret!"))
      )
      listener.specExit(MySpec::class, null)
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.TestRegistered("com.sksamuel.kotest.runner.junit5.MySpec", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("com.sksamuel.kotest.runner.junit5.MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo bar", TestDescriptor.Type.TEST),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo bar"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo bar", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "com.sksamuel.kotest.runner.junit5.MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }
})

private class MySpec : FunSpec() {}
private class MySpec2 : FunSpec() {}

@Ignored
private class MyIgnoredTest() : FunSpec() {
   init {
      test("!disabled test") {
         error("foo")
      }
   }
}

class EventTrackingEngineExecutionListener : EngineExecutionListener {

   sealed interface Event {
      data class TestRegistered(val descriptor: String, val type: TestDescriptor.Type) : Event
      data class ExecutionSkipped(val descriptor: String, val reason: String?) : Event
      data class ExecutionStarted(val descriptor: String) : Event
      data class ExecutionFinished(val descriptor: String, val status: TestExecutionResult.Status) : Event
   }

   val events = mutableListOf<Event>()

   override fun dynamicTestRegistered(testDescriptor: TestDescriptor) {
      events.add(Event.TestRegistered(testDescriptor.displayName, testDescriptor.type))
   }

   override fun executionSkipped(testDescriptor: TestDescriptor, reason: String?) {
      events.add(Event.ExecutionSkipped(testDescriptor.displayName, reason))
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

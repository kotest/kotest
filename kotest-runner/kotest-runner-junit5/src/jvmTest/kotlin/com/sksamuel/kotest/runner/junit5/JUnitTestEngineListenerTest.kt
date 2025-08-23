package com.sksamuel.kotest.runner.junit5

import io.kotest.common.Platform
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.test.TestResult
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
import org.junit.platform.engine.support.descriptor.MethodSource
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class JUnitTestEngineListenerTest : FunSpec({

   val root = createEngineDescriptor(
      uniqueId = UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID),
      specs = listOf(MySpec::class),
      extensions = emptyList()
   )

   val tc1 = TestCase(
      MySpec::class.toDescriptor().append("foo"),
      TestNameBuilder.builder("foo").build(),
      MySpec(),
      {},
      SourceRef.None,
      TestType.Container,
   )

   val tc2 = TestCase(
      tc1.descriptor.append("bar"),
      TestNameBuilder.builder("bar").build(),
      tc1.spec,
      {},
      SourceRef.None,
      TestType.Test,
      parent = tc1,
   )

   val tc3 = TestCase(
      MySpec2::class.toDescriptor().append("baz"),
      TestNameBuilder.builder("baz").build(),
      MySpec2(),
      {},
      SourceRef.None,
      TestType.Container,
   )

   test("an error before spec started should show spec with a dummy error test") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Error(0.seconds, Exception("CRRAACK")))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "Exception",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "Exception"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("Exception"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "Exception",
            TestExecutionResult.Status.FAILED
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.FAILED
         ),
      )
   }

   test("an ignored spec should be skipped") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specIgnored(MySpec::class, "disabled foo")
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped(
            "MySpec",
            "disabled foo"
         )
      )
   }

   test("a successful leaf root test should be marked as started and finished and type TEST") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.Success(12.milliseconds))
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Success(0.seconds))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "foo",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "foo"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("a failed leaf root test should be marked as FAILED and type TEST") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.Failure(5.milliseconds, AssertionError("whack!")))
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Success(0.seconds))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "foo",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "foo"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("an ignored root test should be marked as skipped") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.testIgnored(tc1, "secret!")
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Success(0.seconds))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "foo",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "foo"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("foo", "secret!"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("a successful nested test should be marked as SUCCESSFUL with type TEST") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.testStarted(tc1)
      listener.testStarted(tc2)
      listener.testFinished(tc2, TestResult.Success(3.milliseconds))
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Success(0.seconds))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "bar",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "foo/bar"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("bar"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("bar", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("a failed nested test should be marked as FAILED with type TEST") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.testStarted(tc1)
      listener.testStarted(tc2)
      listener.testFinished(tc2, TestResult.Failure(5.milliseconds, AssertionError("whack!")))
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Success(0.seconds))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "bar",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "foo/bar"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("bar"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("bar", TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("an ignored nested test should be marked as skipped") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.testStarted(tc1)
      listener.testIgnored(tc2, "secret!")
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Success(0.seconds))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "bar",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "foo/bar"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("bar", "secret!"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("an error in the spec should add a placeholder test with the error along with completed tests") {
      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Error(0.seconds, Exception("THWAPP!")))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "foo",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "foo"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "Exception",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "Exception"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("Exception"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("Exception", TestExecutionResult.Status.FAILED),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.FAILED
         ),
      )
   }

   test("state should be reset after spec") {

      val root2 = createEngineDescriptor(
         UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID),
         listOf(MySpec::class, MySpec2::class),
         emptyList(),
      )

      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root2, DisplayNameFormatting(null))

      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.testStarted(tc1)
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Success(0.seconds))

      listener.specStarted(SpecRef.Reference(MySpec2::class))
      listener.testStarted(tc3)
      listener.testFinished(tc3, TestResult.Success(4.milliseconds))
      listener.specFinished(SpecRef.Reference(MySpec2::class), TestResult.Success(0.seconds))

      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "foo",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "foo"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec2"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "baz",
            "com.sksamuel.kotest.runner.junit5.MySpec2",
            "baz"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("baz"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("baz", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec2",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("state should be reset after ignored spec") {

      val root2 = createEngineDescriptor(
         UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID),
         listOf(MySpec::class, MySpec2::class),
         emptyList(),
      )

      val track = EventTrackingEngineExecutionListener()
      val listener = JUnitTestEngineListener(track, root2, DisplayNameFormatting(null))

      listener.specIgnored(MySpec::class, null)

      listener.specStarted(SpecRef.Reference(MySpec2::class))
      listener.testStarted(tc3)
      listener.testFinished(tc3, TestResult.Success(4.milliseconds))
      listener.specFinished(SpecRef.Reference(MySpec2::class), TestResult.Success(0.seconds))

      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionSkipped("MySpec", null),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec2"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "baz",
            "com.sksamuel.kotest.runner.junit5.MySpec2",
            "baz"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("baz"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("baz", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec2",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }

   test("listener should support full test paths") {
      val track = EventTrackingEngineExecutionListener()
      val c = object : AbstractProjectConfig() {
         override val displayFullTestPath: Boolean = true
      }

      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(c))
      listener.engineInitialized(EngineContext.invoke(null, Platform.JVM))
      listener.specStarted(SpecRef.Reference(MySpec::class))
      listener.testStarted(tc1)
      listener.testStarted(tc2)
      listener.testFinished(tc2, TestResult.Success(3.milliseconds))
      listener.testFinished(tc1, TestResult.Success(7.milliseconds))
      listener.specFinished(SpecRef.Reference(MySpec::class), TestResult.Success(0.seconds))
      track.events shouldBe listOf(
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("MySpec"),
         EventTrackingEngineExecutionListener.Event.TestRegistered("foo", TestDescriptor.Type.CONTAINER),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo"),
         EventTrackingEngineExecutionListener.Event.TestCaseRegistered(
            "foo bar",
            "com.sksamuel.kotest.runner.junit5.MySpec",
            "foo/bar"
         ),
         EventTrackingEngineExecutionListener.Event.ExecutionStarted("foo bar"),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo bar", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished("foo", TestExecutionResult.Status.SUCCESSFUL),
         EventTrackingEngineExecutionListener.Event.ExecutionFinished(
            "MySpec",
            TestExecutionResult.Status.SUCCESSFUL
         ),
      )
   }
})

private class MySpec : FunSpec()
private class MySpec2 : FunSpec()

@Ignored
private class MyIgnoredTest : FunSpec() {
   init {
      test("!disabled test") {
         error("foo")
      }
   }
}

class EventTrackingEngineExecutionListener : EngineExecutionListener {

   sealed interface Event {
      data class TestCaseRegistered(val descriptor: String, val className: String, val methodName: String) : Event
      data class TestRegistered(val descriptor: String, val type: TestDescriptor.Type) : Event
      data class ExecutionSkipped(val descriptor: String, val reason: String?) : Event
      data class ExecutionStarted(val descriptor: String) : Event
      data class ExecutionFinished(val descriptor: String, val status: TestExecutionResult.Status) : Event
   }

   val events = mutableListOf<Event>()

   override fun dynamicTestRegistered(testDescriptor: TestDescriptor) {
      if (testDescriptor.type == TestDescriptor.Type.TEST) {
         val source = testDescriptor.source.get() as MethodSource
         events.add(Event.TestCaseRegistered(testDescriptor.displayName, source.className, source.methodName))
      } else {
         events.add(Event.TestRegistered(testDescriptor.displayName, testDescriptor.type))
      }
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

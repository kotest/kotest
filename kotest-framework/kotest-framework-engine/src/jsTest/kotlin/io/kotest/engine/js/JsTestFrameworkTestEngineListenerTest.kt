package io.kotest.engine.js

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.test.TestResultBuilder
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * A recording fake of [KotlinJsTestFramework] that captures suite and test registrations.
 * Suite callbacks are executed synchronously so inner test registrations are captured immediately.
 */
private class RecordingFramework : KotlinJsTestFramework {

   data class RecordedTest(val name: String, val ignored: Boolean, val testFn: () -> Any?)
   data class RecordedSuite(val name: String, val ignored: Boolean, val tests: MutableList<RecordedTest> = mutableListOf())

   val suites = mutableListOf<RecordedSuite>()
   private var currentSuite: RecordedSuite? = null

   override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
      val suite = RecordedSuite(name, ignored)
      suites.add(suite)
      val previous = currentSuite
      currentSuite = suite
      suiteFn()
      currentSuite = previous
   }

   override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
      currentSuite?.tests?.add(RecordedTest(name, ignored, testFn))
   }
}

/** Dummy spec class used purely to satisfy TestCase's spec parameter. */
private class DummySpec : FunSpec()

/** Creates a [SpecRef.Function] with a custom [fqn]. */
private fun specRef(fqn: String): SpecRef =
   SpecRef.Function({ DummySpec() }, DummySpec::class, fqn)

/** Creates a [Descriptor.SpecDescriptor] for the given [fqn]. */
private fun specDescriptor(fqn: String): Descriptor.SpecDescriptor =
   Descriptor.SpecDescriptor(DescriptorId(fqn))

/** Creates a root-level [TestCase] under the spec with [specFqn] and the given test [name]. */
private fun rootTestCase(specFqn: String, name: String): TestCase = TestCase(
   specDescriptor(specFqn).append(name),
   TestNameBuilder.builder(name).build(),
   DummySpec(),
   {},
   SourceRef.None,
   TestType.Test,
)

/** Creates a nested [TestCase] one level deep under a context named [contextName]. */
private fun nestedTestCase(specFqn: String, contextName: String, testName: String): TestCase = TestCase(
   specDescriptor(specFqn).append(contextName).append(testName),
   TestNameBuilder.builder(testName).build(),
   DummySpec(),
   {},
   SourceRef.None,
   TestType.Test,
)

class JsTestFrameworkTestEngineListenerTest {

   // --- engineStarted ---

   @Test
   fun engineStartedRegistersAnchorSuiteNamedKotestEngine() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      listener.engineStarted()
      assertEquals(1, fw.suites.size)
      assertEquals("Kotest Engine", fw.suites[0].name)
      assertFalse(fw.suites[0].ignored)
   }

   @Test
   fun engineStartedRegistersAnchorTestNamedExecutorInsideAnchorSuite() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      listener.engineStarted()
      assertEquals(1, fw.suites[0].tests.size)
      assertEquals("Executor", fw.suites[0].tests[0].name)
      assertFalse(fw.suites[0].tests[0].ignored)
   }

   // --- specIgnored ---

   @Test
   fun specIgnoredRegistersASingleIgnoredSuite() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      listener.specIgnored(DummySpec::class, "disabled")
      assertEquals(1, fw.suites.size)
      assertTrue(fw.suites[0].ignored)
   }

   @Test
   fun specIgnoredReplacesDotsinClassNameWithSpaces() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      listener.specIgnored(DummySpec::class, null)
      assertFalse(fw.suites[0].name.contains('.'))
   }

   @Test
   fun specIgnoredSuiteContainsNoTests() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      listener.specIgnored(DummySpec::class, null)
      assertEquals(0, fw.suites[0].tests.size)
   }

   // --- specFinished: no prior specStarted ---

   @Test
   fun specFinishedDoesNothingWhenSpecStartedWasNeverCalled() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      listener.specFinished(specRef("com.example.MySpec"), TestResultBuilder.builder().build())
      assertEquals(0, fw.suites.size)
   }

   // --- specFinished: suite naming ---

   @Test
   fun specFinishedUsesSpecFqnAsSuiteName() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySuite")
      listener.specStarted(ref)
      val tc = rootTestCase("com.example.MySuite", "a test")
      listener.testStarted(tc)
      listener.testFinished(tc, TestResultBuilder.builder().build())
      listener.specFinished(ref, TestResultBuilder.builder().build())
      assertEquals(1, fw.suites.size)
      assertEquals("com.example.MySuite", fw.suites[0].name)
      assertFalse(fw.suites[0].ignored)
   }

   // --- specFinished: test name stripping ---

   @Test
   fun testNamesHaveSpecFqnPrefixAndSpecDelimiterStripped() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySpec")
      listener.specStarted(ref)
      val tc = rootTestCase("com.example.MySpec", "my test")
      listener.testStarted(tc)
      listener.testFinished(tc, TestResultBuilder.builder().build())
      listener.specFinished(ref, TestResultBuilder.builder().build())
      assertEquals("my test", fw.suites[0].tests[0].name)
   }

   // --- specFinished: dot escaping ---

   @Test
   fun dotsInTestNamesAreReplacedWithSpaces() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySpec")
      listener.specStarted(ref)
      val tc = rootTestCase("com.example.MySpec", "test.with.dots")
      listener.testStarted(tc)
      listener.testFinished(tc, TestResultBuilder.builder().build())
      listener.specFinished(ref, TestResultBuilder.builder().build())
      assertEquals("test with dots", fw.suites[0].tests[0].name)
   }

   // --- specFinished: nested test delimiter ---

   @Test
   fun nestedTestNamesUseChevronDelimiter() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySpec")
      listener.specStarted(ref)
      val tc = nestedTestCase("com.example.MySpec", "my context", "my test")
      listener.testStarted(tc)
      listener.testFinished(tc, TestResultBuilder.builder().build())
      listener.specFinished(ref, TestResultBuilder.builder().build())
      assertEquals("my context » my test", fw.suites[0].tests[0].name)
   }

   // --- specFinished: registration order ---

   @Test
   fun testsAppearInRegistrationOrder() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySpec")
      listener.specStarted(ref)
      val tc1 = rootTestCase("com.example.MySpec", "first")
      val tc2 = rootTestCase("com.example.MySpec", "second")
      val tc3 = rootTestCase("com.example.MySpec", "third")
      listener.testStarted(tc1)
      listener.testStarted(tc2)
      listener.testStarted(tc3)
      listener.testFinished(tc1, TestResultBuilder.builder().build())
      listener.testFinished(tc2, TestResultBuilder.builder().build())
      listener.testFinished(tc3, TestResultBuilder.builder().build())
      listener.specFinished(ref, TestResultBuilder.builder().build())
      assertEquals("first", fw.suites[0].tests[0].name)
      assertEquals("second", fw.suites[0].tests[1].name)
      assertEquals("third", fw.suites[0].tests[2].name)
   }

   // --- specFinished: ignored tests ---

   @Test
   fun ignoredTestsAreRegisteredWithIgnoredTrue() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySpec")
      listener.specStarted(ref)
      val tc = rootTestCase("com.example.MySpec", "skipped test")
      listener.testIgnored(tc, "just because")
      listener.specFinished(ref, TestResultBuilder.builder().build())
      assertTrue(fw.suites[0].tests[0].ignored)
   }

   // --- specFinished: passing tests ---

   @Test
   fun passingTestsAreRegisteredWithIgnoredFalse() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySpec")
      listener.specStarted(ref)
      val tc = rootTestCase("com.example.MySpec", "passing test")
      listener.testStarted(tc)
      listener.testFinished(tc, TestResultBuilder.builder().build())
      listener.specFinished(ref, TestResultBuilder.builder().build())
      assertFalse(fw.suites[0].tests[0].ignored)
   }

   // --- specFinished: error result ---

   @Test
   fun testFinishedWithErrorRegistersTestAsNotIgnored() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySpec")
      listener.specStarted(ref)
      val tc = rootTestCase("com.example.MySpec", "failing test")
      listener.testStarted(tc)
      listener.testFinished(tc, TestResultBuilder.builder().withError(RuntimeException("boom")).build())
      listener.specFinished(ref, TestResultBuilder.builder().build())
      // A failing test must not be ignored — Mocha needs to execute it so the failure is reported
      assertFalse(fw.suites[0].tests[0].ignored)
   }

   // --- specFinished: empty spec ---

   @Test
   fun specWithNoTestsRegistersAnEmptySuiteWhenSpecStartedWasCalled() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySpec")
      listener.specStarted(ref)
      // no testStarted calls — spec has no tests
      listener.specFinished(ref, TestResultBuilder.builder().build())
      // specFinished only skips when specStarted was never called (proxies entry is null)
      // when specStarted was called, an empty suite is registered
      assertEquals(1, fw.suites.size)
      assertEquals(0, fw.suites[0].tests.size)
   }


   // --- testIgnored without specStarted ---

   @Test
   fun testIgnoredWithoutPriorSpecStartedDoesNotCrash() = runTest {
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val tc = rootTestCase("com.example.MySpec", "orphan test")
      // Should silently do nothing — no entry in proxies for this spec
      listener.testIgnored(tc, null)
      assertEquals(0, fw.suites.size)
   }

   // --- testFinished updates proxy result ---

   @Test
   fun testFinishedUpdatesProxyResultFromSuccessToIgnored() = runTest {
      // testStarted registers a Success proxy; testFinished should overwrite with Ignored
      val fw = RecordingFramework()
      val listener = JsTestFrameworkTestEngineListener(fw)
      val ref = specRef("com.example.MySpec")
      listener.specStarted(ref)
      val tc = rootTestCase("com.example.MySpec", "a test")
      listener.testStarted(tc)
      // Overwrite with an ignored result via testFinished
      listener.testFinished(tc, TestResultBuilder.builder().withIgnoreReason("late ignore").build())
      listener.specFinished(ref, TestResultBuilder.builder().build())
      // The final proxy result should be ignored, so it's registered with ignored=true
      assertTrue(fw.suites[0].tests[0].ignored)
   }
}

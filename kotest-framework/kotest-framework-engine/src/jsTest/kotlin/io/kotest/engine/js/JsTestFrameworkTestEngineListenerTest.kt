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
import io.kotest.matchers.shouldBe

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

class JsTestFrameworkTestEngineListenerTest : FunSpec({

   context("engineStarted") {

      test("registers an anchor suite named 'Kotest'") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         listener.engineStarted()
         fw.suites.size shouldBe 1
         fw.suites[0].name shouldBe "Kotest"
         fw.suites[0].ignored shouldBe false
      }

      test("registers an 'Executor' test inside the anchor suite") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         listener.engineStarted()
         fw.suites[0].tests.size shouldBe 1
         fw.suites[0].tests[0].name shouldBe "Executor"
         fw.suites[0].tests[0].ignored shouldBe false
      }
   }

   context("specIgnored") {

      test("registers a single ignored suite") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         listener.specIgnored(DummySpec::class, "disabled")
         fw.suites.size shouldBe 1
         fw.suites[0].ignored shouldBe true
      }

      test("replaces dots in the class name with spaces") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         listener.specIgnored(DummySpec::class, null)
         fw.suites[0].name.contains('.') shouldBe false
      }

      test("registers no tests inside the ignored suite") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         listener.specIgnored(DummySpec::class, null)
         fw.suites[0].tests.size shouldBe 0
      }
   }

   context("specFinished") {

      test("does nothing when specStarted was never called") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         listener.specFinished(specRef("com.example.MySpec"), TestResultBuilder.builder().build())
         fw.suites.size shouldBe 0
      }

      test("uses the spec fqn as the suite name") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val ref = specRef("com.example.MySuite")
         listener.specStarted(ref)
         val tc = rootTestCase("com.example.MySuite", "a test")
         listener.testStarted(tc)
         listener.testFinished(tc, TestResultBuilder.builder().build())
         listener.specFinished(ref, TestResultBuilder.builder().build())
         fw.suites.size shouldBe 1
         fw.suites[0].name shouldBe "com.example.MySuite"
         fw.suites[0].ignored shouldBe false
      }

      test("strips the spec fqn prefix and spec delimiter from test names") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val ref = specRef("com.example.MySpec")
         listener.specStarted(ref)
         val tc = rootTestCase("com.example.MySpec", "my test")
         listener.testStarted(tc)
         listener.testFinished(tc, TestResultBuilder.builder().build())
         listener.specFinished(ref, TestResultBuilder.builder().build())
         fw.suites[0].tests[0].name shouldBe "my test"
      }

      test("replaces dots in test names with spaces") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val ref = specRef("com.example.MySpec")
         listener.specStarted(ref)
         val tc = rootTestCase("com.example.MySpec", "test.with.dots")
         listener.testStarted(tc)
         listener.testFinished(tc, TestResultBuilder.builder().build())
         listener.specFinished(ref, TestResultBuilder.builder().build())
         fw.suites[0].tests[0].name shouldBe "test with dots"
      }

      test("uses the » delimiter for nested test names") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val ref = specRef("com.example.MySpec")
         listener.specStarted(ref)
         val tc = nestedTestCase("com.example.MySpec", "my context", "my test")
         listener.testStarted(tc)
         listener.testFinished(tc, TestResultBuilder.builder().build())
         listener.specFinished(ref, TestResultBuilder.builder().build())
         fw.suites[0].tests[0].name shouldBe "my context » my test"
      }

      test("tests appear in registration order") {
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
         fw.suites[0].tests[0].name shouldBe "first"
         fw.suites[0].tests[1].name shouldBe "second"
         fw.suites[0].tests[2].name shouldBe "third"
      }

      test("ignored tests are registered with ignored=true") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val ref = specRef("com.example.MySpec")
         listener.specStarted(ref)
         val tc = rootTestCase("com.example.MySpec", "skipped test")
         listener.testIgnored(tc, "just because")
         listener.specFinished(ref, TestResultBuilder.builder().build())
         fw.suites[0].tests[0].ignored shouldBe true
      }

      test("passing tests are registered with ignored=false") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val ref = specRef("com.example.MySpec")
         listener.specStarted(ref)
         val tc = rootTestCase("com.example.MySpec", "passing test")
         listener.testStarted(tc)
         listener.testFinished(tc, TestResultBuilder.builder().build())
         listener.specFinished(ref, TestResultBuilder.builder().build())
         fw.suites[0].tests[0].ignored shouldBe false
      }

      test("a test finished with an error is registered with ignored=false") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val ref = specRef("com.example.MySpec")
         listener.specStarted(ref)
         val tc = rootTestCase("com.example.MySpec", "failing test")
         listener.testStarted(tc)
         listener.testFinished(tc, TestResultBuilder.builder().withError(RuntimeException("boom")).build())
         listener.specFinished(ref, TestResultBuilder.builder().build())
         // a failing test must not be marked ignored — Mocha needs to execute it so the failure is reported
         fw.suites[0].tests[0].ignored shouldBe false
      }

      test("a spec with no tests registers an empty suite") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val ref = specRef("com.example.MySpec")
         listener.specStarted(ref)
         // no testStarted calls
         listener.specFinished(ref, TestResultBuilder.builder().build())
         // early return only fires when specStarted was never called; an empty list still produces a suite
         fw.suites.size shouldBe 1
         fw.suites[0].tests.size shouldBe 0
      }
   }

   context("testIgnored") {

      test("does not crash when specStarted was never called") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val tc = rootTestCase("com.example.MySpec", "orphan test")
         listener.testIgnored(tc, null)
         fw.suites.size shouldBe 0
      }
   }

   context("testFinished") {

      test("overwrites the proxy result so the final state is reflected at specFinished") {
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)
         val ref = specRef("com.example.MySpec")
         listener.specStarted(ref)
         val tc = rootTestCase("com.example.MySpec", "a test")
         listener.testStarted(tc)
         // overwrite the initial Success proxy with an Ignored result
         listener.testFinished(tc, TestResultBuilder.builder().withIgnoreReason("late ignore").build())
         listener.specFinished(ref, TestResultBuilder.builder().build())
         fw.suites[0].tests[0].ignored shouldBe true
      }
   }

   context("engineFinished") {

      test("spec suite is registered with the framework during specFinished, not deferred to engineFinished") {
         // Regression guard: suites must be registered in specFinished so that all framework.suite() calls
         // happen before channel.send(Unit) in engineFinished. If registration were deferred to
         // engineFinished, mocha would have already advanced past the executor before suites exist.
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)

         val ref = specRef("com.example.MySpec")
         listener.specStarted(ref)
         val tc = rootTestCase("com.example.MySpec", "a test")
         listener.testStarted(tc)
         listener.testFinished(tc, TestResultBuilder.builder().build())
         listener.specFinished(ref, TestResultBuilder.builder().build())

         // suite must be visible before engineFinished is called
         fw.suites.size shouldBe 1
         fw.suites[0].name shouldBe "com.example.MySpec"

         listener.engineFinished(emptyList())

         // engineFinished only releases the channel — it must not register any additional suites
         fw.suites.size shouldBe 1
      }

      test("all spec suites from multiple specs are registered before engineFinished is called") {
         // Regression guard: with multiple specs each suite must be visible after its own specFinished,
         // long before engineFinished sends the channel. A per-spec channel send (the pre-fix behaviour)
         // would release mocha before the remaining specs are registered.
         val fw = RecordingFramework()
         val listener = JsTestFrameworkTestEngineListener(fw)

         listOf("com.example.Spec1", "com.example.Spec2", "com.example.Spec3").forEach { fqn ->
            val ref = specRef(fqn)
            listener.specStarted(ref)
            val tc = rootTestCase(fqn, "a test")
            listener.testStarted(tc)
            listener.testFinished(tc, TestResultBuilder.builder().build())
            listener.specFinished(ref, TestResultBuilder.builder().build())
         }

         // all three spec suites are registered before engineFinished is called
         fw.suites.size shouldBe 3
         fw.suites[0].name shouldBe "com.example.Spec1"
         fw.suites[1].name shouldBe "com.example.Spec2"
         fw.suites[2].name shouldBe "com.example.Spec3"

         listener.engineFinished(emptyList())

         // engineFinished must not register any new suites
         fw.suites.size shouldBe 3
      }
   }
})

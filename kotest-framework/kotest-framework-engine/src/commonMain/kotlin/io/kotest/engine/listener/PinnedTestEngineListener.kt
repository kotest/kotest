package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import kotlin.reflect.KClass

/**
 * Wraps a [TestEngineListener] to ensure that, within a single spec, only one test's start/finish
 * notifications are passed to the delegate at a time -- unless the new test is a nested
 * descendant of whatever is currently open, which is forwarded immediately since that's normal
 * nesting, not a race. Notifications for any other (non-descendant) test are queued until the
 * currently open test, and anything nested inside it, has fully finished.
 *
 * This is a test-level analog of [PinnedSpecTestEngineListener], needed because that class only
 * pins at spec granularity: once a spec is the "running" one, all of ITS OWN tests'
 * start/finish notifications pass through unbuffered, including tests running genuinely
 * concurrently under TestExecutionMode.Concurrent. That's fine for consumers that track
 * hierarchy structurally (eg JUnit Platform's EngineExecutionListener, keyed by TestDescriptor),
 * but breaks consumers that infer hierarchy purely from message ORDER (eg TeamCity service
 * messages, parsed by Gradle's Kotlin/Native test runner or IntelliJ's non-Gradle JVM launcher),
 * which assume strict LIFO nesting of start/finish pairs.
 *
 * TODO: this currently serializes concurrent siblings in "whichever test's testStarted call wins
 * the race to arrive first" order, not necessarily declaration order. If callers need output to
 * always match declaration order, this needs to hold each test's notification until its declared
 * turn rather than releasing on first-arrival.
 *
 * TODO: holding a slower test's node open for its whole real duration means a faster sibling's
 * *unrelated* console output (eg from a plain [ConsoleTestEngineListener], which this class does
 * not wrap) can still print while the slower test's node is the one open at the TC layer, and
 * consumers that attribute stdout to "whichever TC node is currently open" will show it nested under the wrong
 * test. Pass/fail/timing results are unaffected; this is a console-output-attribution artifact.
 *

 * Note: This class is not thread safe. It is up to the caller to ensure that calls
 * to the methods of this listener are strictly sequential, for example, by using
 * an instance of [ThreadSafeTestEngineListener].
 */
@KotestInternal
class PinnedTestEngineListener(val listener: TestEngineListener) : TestEngineListener {

   // the chain of currently open test descriptors, outermost first, matching what a LIFO
   // start/finish stack (eg TeamCity service messages) expects to see
   private val openStack = mutableListOf<Descriptor>()
   private val callbacks = mutableListOf<suspend () -> Unit>()

   private fun queue(fn: suspend () -> Unit) {
      callbacks.add { fn() }
   }

   private suspend fun replay() {
      val _callbacks = callbacks.toList()
      callbacks.clear()
      _callbacks.forEach { it.invoke() }
   }

   private fun canOpen(descriptor: Descriptor): Boolean {
      val top = openStack.lastOrNull() ?: return true
      return top.isParentOf(descriptor)
   }

   override suspend fun engineStarted() {
      listener.engineStarted()
   }

   override suspend fun engineInitialized(context: TestEngineInitializedContext) {
      listener.engineInitialized(context)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      listener.engineFinished(t)
   }

   override suspend fun specStarted(ref: SpecRef) {
      listener.specStarted(ref)
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      listener.specFinished(ref, result)
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      listener.specIgnored(kclass, reason)
   }

   override suspend fun testStarted(testCase: TestCase) {
      if (canOpen(testCase.descriptor)) {
         openStack.add(testCase.descriptor)
         listener.testStarted(testCase)
      } else {
         queue {
            testStarted(testCase)
         }
      }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      if (openStack.lastOrNull() == testCase.descriptor) {
         openStack.removeAt(openStack.lastIndex)
         listener.testFinished(testCase, result)
         replay()
      } else {
         queue {
            testFinished(testCase, result)
         }
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      if (canOpen(testCase.descriptor)) {
         listener.testIgnored(testCase, reason)
      } else {
         queue {
            testIgnored(testCase, reason)
         }
      }
   }
}

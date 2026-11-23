package io.kotest.engine.test

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestCase
import io.kotest.engine.test.output.SpecThreadOwner
import io.kotest.engine.test.output.TestOutputBuffer
import kotlinx.coroutines.ThreadContextElement
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Gives access to the [TestCase] that is executing on the current thread.
 *
 * Kotest tracks the executing test in the coroutine context, and sets it on whichever thread a coroutine
 * of that test resumes on. This includes child coroutines, `withContext` blocks and dispatcher switches.
 * Because the lookup is not a suspending function, it can be used from code that knows nothing about
 * coroutines, such as a logging appender or an `OutputStream`.
 *
 * Threads that do not run a coroutine of a test, such as an application's own thread pools, have no current
 * test. Use [asContextElement] or [wrap] to carry the current test onto those.
 *
 * JVM only.
 */
@ExperimentalKotest
object CurrentTestCase {

   /**
    * Returns the [TestCase] executing on the current thread, or null if this thread is not running a test.
    */
   fun get(): TestCase? = TestThreadState.current.get()?.testCase

   /**
    * Returns a coroutine context element that makes the current test the current test of any coroutine
    * started with it, for example a coroutine launched in a scope the test does not own.
    * Returns [EmptyCoroutineContext] when there is no current test.
    */
   fun asContextElement(): CoroutineContext =
      TestThreadState.current.get()?.let { TestCaseContextElement(it) } ?: EmptyCoroutineContext

   /**
    * Returns a [Runnable] that runs [runnable] with the current test as its current test,
    * for handing work to an executor. Returns [runnable] unchanged when there is no current test.
    */
   fun wrap(runnable: Runnable): Runnable {
      val state = TestThreadState.current.get() ?: return runnable
      return Runnable {
         val previous = TestThreadState.current.get()
         TestThreadState.current.set(state)
         try {
            runnable.run()
         } finally {
            TestThreadState.current.set(previous)
         }
      }
   }

   /**
    * Returns a [ThreadFactory] whose threads never belong to a spec, for pools that are shared across specs.
    *
    * With [io.kotest.core.config.AbstractProjectConfig.outputCaptureSpecThreads] enabled, a thread belongs to
    * the spec that was running where the thread was created. A pool that grows lazily creates its threads
    * wherever work is submitted, so a pool shared across specs would otherwise hand its threads to whichever
    * spec happened to submit work when a thread was added. Output from threads created by this factory, and
    * from any threads they create, is written straight through instead.
    *
    * For example, for a pool shared by several specs:
    *
    * ```
    * object SharedWorkers {
    *    val pool = Executors.newCachedThreadPool(CurrentTestCase.unownedThreadFactory())
    * }
    * ```
    *
    * @param factory the factory that creates the threads; its names, daemon flag and priorities are kept.
    */
   fun unownedThreadFactory(factory: ThreadFactory = Executors.defaultThreadFactory()): ThreadFactory =
      ThreadFactory { runnable ->
         factory.newThread {
            SpecThreadOwner.set(null)
            runnable.run()
         }
      }
}

/**
 * What a thread needs to know about the test it is running: the test itself, and where its output goes
 * when output capture is enabled.
 *
 * [excluded] marks engine code, such as listeners, that runs inside a test's coroutine but whose output is not
 * the test's: it has no current test, and its output is never attributed to one.
 */
internal class TestThreadState(val testCase: TestCase?, val output: TestOutputBuffer?) {

   /**
    * How deeply nested the test is, so that when a spec runs its tests one at a time,
    * the innermost running test can be found.
    */
   val depth: Int = generateSequence(testCase) { it.parent }.count()

   companion object {
      val current = ThreadLocal<TestThreadState?>()
      val excluded = TestThreadState(null, null)
   }
}

/**
 * Sets [TestThreadState.current] on every thread a coroutine with this element resumes on,
 * and restores the previous value when it suspends.
 */
internal class TestCaseContextElement(
   private val state: TestThreadState,
) : AbstractCoroutineContextElement(Key), ThreadContextElement<TestThreadState?> {

   companion object Key : CoroutineContext.Key<TestCaseContextElement>

   override fun updateThreadContext(context: CoroutineContext): TestThreadState? {
      val previous = TestThreadState.current.get()
      TestThreadState.current.set(state)
      return previous
   }

   override fun restoreThreadContext(context: CoroutineContext, oldState: TestThreadState?) {
      TestThreadState.current.set(oldState)
   }

   override fun toString(): String = "TestCaseContextElement(${state.testCase?.descriptor?.path()?.value})"
}

package io.kotest.engine.test.output

import io.kotest.common.JVMOnly
import io.kotest.core.spec.SpecRef
import io.kotest.engine.test.TestThreadState
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinWorkerThread
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

@JVMOnly
internal actual suspend fun <T> withSpecThreadOwnership(ref: SpecRef, enabled: Boolean, block: suspend () -> T): T {
   if (!enabled) return block()
   val owner = SpecThreadOwner(ref)
   return try {
      withContext(SpecThreadOwnerElement(owner)) { block() }
   } finally {
      owner.finish()
   }
}

/**
 * Attributes output from threads that no test coroutine is running on, but that were created while a spec was
 * running, such as the worker threads of a server the spec started. Such a thread inherits the spec through an
 * [InheritableThreadLocal] when it is created, the same way Rust's test runner hands a test's output capture to
 * the threads that test spawns.
 *
 * Its output goes to the spec's running test, but only when that test is unambiguous: the spec's running tests
 * form a single chain of a test and its parents, which holds whenever the spec runs its tests one at a time,
 * whatever the concurrency between specs. Otherwise, and once the spec has finished, the output passes through.
 *
 * Inheriting at thread creation is wrong for pools that are shared by the whole JVM but create their threads
 * lazily, since a pool thread keeps the spec that happened to trigger its creation. This is why Logback stopped
 * inheriting its MDC (LOGBACK-422). The JVM's and kotlinx.coroutines' shared pools are therefore never attributed
 * this way; other shared pools an application creates lazily can still be misattributed, which is why this is
 * opt-in.
 */
internal class SpecThreadOwner(private val ref: SpecRef) {

   private val running = ConcurrentLinkedDeque<TestThreadState>()

   @Volatile
   private var finished = false

   fun started(state: TestThreadState) {
      if (!finished) running.add(state)
   }

   fun finished(state: TestThreadState) {
      running.remove(state)
   }

   fun finish() {
      finished = true
      running.clear()
   }

   /**
    * Returns the buffer of the spec's innermost running test, or null if the spec has finished, if it is
    * running more than one test at once, in which case the output cannot be attributed, or if the innermost
    * test does not capture its output.
    */
   fun target(): TestOutputBuffer? {
      if (finished) return null
      val tests = running.toList()
      val innermost = tests.maxByOrNull { it.depth } ?: return null
      val descriptor = innermost.testCase?.descriptor ?: return null
      val chain = tests.all { it === innermost || it.testCase?.descriptor?.isAncestorOf(descriptor) == true }
      return if (chain) innermost.output else null
   }

   override fun toString(): String = "SpecThreadOwner(${ref.fqn})"

   companion object {

      private val inherited = InheritableThreadLocal<SpecThreadOwner?>()

      /**
       * The buffer that output written on the current thread is attributed to through spec ownership, if any.
       */
      fun target(): TestOutputBuffer? {
         val owner = inherited.get() ?: return null
         if (isSharedPoolThread(Thread.currentThread())) return null
         return owner.target()
      }

      private fun isSharedPoolThread(thread: Thread): Boolean =
         (thread is ForkJoinWorkerThread && thread.pool === ForkJoinPool.commonPool()) ||
            thread.javaClass.name.startsWith("kotlinx.coroutines.")

      /**
       * Makes [owner] the current thread's owner, returning the previous one.
       */
      internal fun swap(owner: SpecThreadOwner?): SpecThreadOwner? = inherited.get().also { inherited.set(owner) }

      /**
       * Makes [owner] the current thread's owner, for example null for a thread that must never belong to a spec.
       */
      internal fun set(owner: SpecThreadOwner?) {
         inherited.set(owner)
      }
   }
}

/**
 * Sets the spec's [SpecThreadOwner] on every thread a coroutine of the spec runs on, so that any thread created
 * from there inherits it.
 */
internal class SpecThreadOwnerElement(
   val owner: SpecThreadOwner,
) : AbstractCoroutineContextElement(Key), ThreadContextElement<SpecThreadOwner?> {

   companion object Key : CoroutineContext.Key<SpecThreadOwnerElement>

   override fun updateThreadContext(context: CoroutineContext): SpecThreadOwner? = SpecThreadOwner.swap(owner)

   override fun restoreThreadContext(context: CoroutineContext, oldState: SpecThreadOwner?) {
      SpecThreadOwner.set(oldState)
   }
}

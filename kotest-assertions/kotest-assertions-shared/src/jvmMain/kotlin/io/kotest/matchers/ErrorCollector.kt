@file:JvmName("jvmerrorcollector")

package io.kotest.matchers

import kotlinx.coroutines.CopyableThreadContextElement
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.CoroutineContext

actual val errorCollector: ErrorCollector get() = threadLocalErrorCollector.get()

/**
 * A [CoroutineContext.Element] which keeps the error collector synchronized with thread-switching coroutines.
 *
 * When using [io.kotest.assertions.withClue] or [io.kotest.assertions.assertSoftly] on the JVM without
 * the Kotest framework, this context element should be added to each top-level coroutine context, e.g. via
 * - `runBlocking(errorCollectorContextElement) { ... }`
 * - `runTest(Dispatchers.IO + errorCollectorContextElement) { ... }`
 */
val errorCollectorContextElement: CoroutineContext.Element
   get() = ErrorCollectorContextElement(threadLocalErrorCollector.get())


private val threadLocalErrorCollector = ThreadLocal.withInitial { CoroutineLocalErrorCollector() }


private class CoroutineLocalErrorCollector : BasicErrorCollector() {
   fun copy(): CoroutineLocalErrorCollector {
      val result = CoroutineLocalErrorCollector()
      result.failures.addAll(failures)
      result.mode = mode
      result.clues.addAll(clues)
      return result
   }
}


@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private class ErrorCollectorContextElement(private val coroutineLocalErrorCollector: CoroutineLocalErrorCollector) :
   CopyableThreadContextElement<CoroutineLocalErrorCollector> {

   override val key: CoroutineContext.Key<ErrorCollectorContextElement> = Key

   override fun updateThreadContext(context: CoroutineContext): CoroutineLocalErrorCollector {
      val oldState = threadLocalErrorCollector.get()
      threadLocalErrorCollector.set(coroutineLocalErrorCollector)
      return oldState
   }

   override fun restoreThreadContext(context: CoroutineContext, oldState: CoroutineLocalErrorCollector) {
      threadLocalErrorCollector.set(oldState)
   }

   companion object Key : CoroutineContext.Key<ErrorCollectorContextElement>

   override fun copyForChild(): CopyableThreadContextElement<CoroutineLocalErrorCollector> =
      ErrorCollectorContextElement(threadLocalErrorCollector.get().copy())

   override fun mergeForChild(overwritingElement: CoroutineContext.Element): CoroutineContext =
      copyForChild()
}


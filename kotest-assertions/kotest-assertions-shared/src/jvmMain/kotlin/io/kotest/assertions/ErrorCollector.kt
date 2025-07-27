@file:JvmName("jvmerrorcollector")

package io.kotest.assertions

import io.kotest.assertions.print.Printed
import kotlinx.coroutines.CopyableThreadContextElement
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.CoroutineContext

actual val errorCollector: ErrorCollector get() = threadLocalErrorCollector.get()

/**
 * A [CoroutineContext.Element] which keeps the error collector synchronized with thread-switching coroutines.
 *
 * When using [withClue] or [assertSoftly] on the JVM without the Kotest framework, this context element
 * should be added to each top-level coroutine context, e.g. via
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

actual fun ErrorCollector.collectiveError(): AssertionError? {
   fun prefixWithSubjectInformation(e: AssertionFailedError, subject: Printed) =
      JvmAssertionFailedError(
         "The following assertion for ${subject.value} failed:\n" + e.message,
         e.cause,
         e.expectedValue,
         e.actualValue
      )

   val failures = errors()
   clear()

   return if (failures.size == 1 && failures[0] is AssertionFailedError) {
      val e = failures[0] as AssertionFailedError
      subject?.let { prefixWithSubjectInformation(e, it) } ?: e
   } else {
      failures.toAssertionError(depth, subject)
   }
}


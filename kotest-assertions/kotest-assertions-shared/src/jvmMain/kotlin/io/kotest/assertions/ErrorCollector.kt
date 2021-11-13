@file:JvmName("jvmerrorcollector")

package io.kotest.assertions

import kotlinx.coroutines.asContextElement

actual val errorCollector: ErrorCollector get() = ThreadLocalErrorCollector.instance.get()

/**
 * A [kotlin.coroutines.CoroutineContext.Element] which keeps the error collector synchronized with thread-switching coroutines.
 *
 * When using [withClue] or [assertSoftly] on the JVM without the Kotest framework, this context element
 * should be added to each top-level coroutine context, e.g. via
 * - runBlocking(errorCollectorContextElement) { ... }
 * - runBlockingTest(Dispatchers.IO + errorCollectorContextElement) { ... }
 */
val errorCollectorContextElement get() = ThreadLocalErrorCollector.instance.asContextElement()

class ThreadLocalErrorCollector : BasicErrorCollector() {
   companion object {
      val instance = object : ThreadLocal<ErrorCollector>() {
         override fun initialValue() = ThreadLocalErrorCollector()
      }
   }
}

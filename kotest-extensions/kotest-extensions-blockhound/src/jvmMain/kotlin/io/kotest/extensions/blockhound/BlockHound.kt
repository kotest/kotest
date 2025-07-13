package io.kotest.extensions.blockhound

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

enum class BlockHoundMode {
   DISABLED,
   ERROR,
   PRINT,
}

class BlockHound(private val mode: BlockHoundMode = BlockHoundMode.ERROR) : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      initialize()

      return withBlockHoundMode(mode) { execute(testCase) }
   }

   class ContextElement(private val mode: BlockHoundMode) : ThreadContextElement<BlockHoundMode> {
      override val key: CoroutineContext.Key<ContextElement> = Key

      override fun updateThreadContext(context: CoroutineContext): BlockHoundMode {
         // invoked before the coroutine is resumed on the current thread
         val oldState = threadLocalMode.get()
         threadLocalMode.set(mode)
         return oldState
      }

      override fun restoreThreadContext(context: CoroutineContext, oldState: BlockHoundMode) {
         // invoked after the coroutine has suspended on the current thread
         threadLocalMode.set(oldState)
      }

      object Key : CoroutineContext.Key<ContextElement>
   }

   companion object {
      private var isInitialized = false
      private var threadLocalMode = ThreadLocal.withInitial { BlockHoundMode.DISABLED }
      internal val effectiveMode: BlockHoundMode get() = threadLocalMode.get()

      private fun initialize() {
         if (!isInitialized) {
            reactor.blockhound.BlockHound.install()
            isInitialized = true
         }
      }
   }
}

/**
 * Execute [block] in a coroutine scope governed by the specified blockhound [mode].
 *
 * Example:
 * ```
 *     withBlockHoundMode(BlockHoundMode.DISABLED) { someBlockingCall() }
 * ```
 */
suspend fun <R> withBlockHoundMode(mode: BlockHoundMode, block: suspend () -> R): R =
   withContext(BlockHound.ContextElement(mode)) {
      block()
   }

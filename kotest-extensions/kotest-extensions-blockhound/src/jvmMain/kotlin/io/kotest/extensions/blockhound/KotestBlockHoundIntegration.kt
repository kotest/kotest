package io.kotest.extensions.blockhound

import reactor.blockhound.BlockingOperationError
import reactor.blockhound.integration.BlockHoundIntegration
import reactor.blockhound.BlockHound.Builder as BlockHoundBuilder

class KotestBlockHoundIntegration : BlockHoundIntegration {
   override fun applyTo(builder: BlockHoundBuilder): Unit = with(builder) {
      // Kotest uses `ThreadPoolExecutor` via `Executors.newSingleThreadExecutor().asCoroutineDispatcher()`.
      // `ThreadPoolExecutor` uses `LinkedBlockingQueue` for thread management, which may briefly block.
      allowBlockingCallsInside("java.util.concurrent.ThreadPoolExecutor", "execute")

      // Allow blocking calls when processing test engine notifications.
      listOf(
         "executionFinished",
         "reportingEntryPublished",
         "executionSkipped",
         "executionStarted",
         "dynamicTestRegistered"
      ).forEach { method ->
         allowBlockingCallsInside("io.kotest.runner.junit.platform.SynchronizedEngineExecutionListener", method)
      }

      // Allow blocking calls used when running Kotest in debug mode.
      allowBlockingCallsInside("io.kotest.mpp.WriteLogKt", "writeLog")
      allowBlockingCallsInside("kotlin.jvm.internal.Reflection", "renderLambdaToString")

      blockingMethodCallback {
         when (BlockHound.effectiveMode) {
            BlockHoundMode.ERROR -> throw BlockingOperationError(it)
            BlockHoundMode.PRINT -> BlockingOperationError(it).printStackTrace()
            BlockHoundMode.DISABLED -> {}
         }
      }
   }
}

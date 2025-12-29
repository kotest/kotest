package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

/**
 * A no-op implementation of KSPLogger for testing purposes.
 * All logging methods are empty and do nothing.
 */
object NoOpKSPLogger : KSPLogger {
   override fun error(message: String, symbol: KSNode?) {
   }

   override fun exception(e: Throwable) {
   }

   override fun info(message: String, symbol: KSNode?) {
   }

   override fun logging(message: String, symbol: KSNode?) {
   }

   override fun warn(message: String, symbol: KSNode?) {
   }
}

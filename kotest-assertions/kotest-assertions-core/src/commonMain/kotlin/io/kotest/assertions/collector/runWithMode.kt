package io.kotest.assertions.collector

import io.kotest.matchers.ErrorCollectionMode
import io.kotest.matchers.ErrorCollector

/**
 * Runs the given [block] with the specified [mode] for error collection.
 *
 * The original collection mode is restored after the block execution.
 *
 * @param mode the error collection mode to set during the block execution.
 * @param block the code block to execute with the specified error collection mode.
 * @return the result of the block execution.
 */
inline fun <reified T> ErrorCollector.runWithMode(mode: ErrorCollectionMode, block: () -> T): T {
   return getCollectionMode().let { original ->
      setCollectionMode(mode)
      try {
         block()
      } finally {
         setCollectionMode(original)
      }
   }
}

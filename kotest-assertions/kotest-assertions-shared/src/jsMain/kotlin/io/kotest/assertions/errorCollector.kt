package io.kotest.assertions

import io.kotest.mpp.stacktraces

actual val errorCollector: ErrorCollector = JsErrorCollector

object JsErrorCollector : BasicErrorCollector()

actual fun ErrorCollector.collectiveError(): AssertionError? {
   val failures = errors()
   clear()
   return if (failures.isNotEmpty()) {
      if (failures.size == 1) {
         AssertionError(failures[0].message).also {
            stacktraces.cleanStackTrace(it)
         }
      } else {
         MultiAssertionError(failures).also {
            stacktraces.cleanStackTrace(it) // cleans the creation of MultiAssertionError
         }
      }
   } else null
}

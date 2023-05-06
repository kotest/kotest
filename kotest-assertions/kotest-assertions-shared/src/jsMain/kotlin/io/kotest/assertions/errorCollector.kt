package io.kotest.assertions

import io.kotest.mpp.stacktraces

actual val errorCollector: ErrorCollector = JsErrorCollector

object JsErrorCollector : BasicErrorCollector()

actual fun ErrorCollector.collectiveError(): AssertionError? {
   val failures = errors()
   clear()
   return failures.toAssertionError(depth, subject)
}


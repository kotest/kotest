package io.kotest.assertions

actual val errorCollector: ErrorCollector = NoopErrorCollector

actual fun ErrorCollector.collectiveError(): AssertionError? {
   val failures = errors()
   clear()
   return failures.toAssertionError(depth, subject)
}

package io.kotest.assertions

actual fun ErrorCollector.collectErrors(): AssertionError? {
   val failures = errors()
   clear()
   return failures.toAssertionError(depth, subject)
}

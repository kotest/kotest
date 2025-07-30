package io.kotest.assertions

import io.kotest.matchers.ErrorCollector

actual fun ErrorCollector.collectErrors(): AssertionError? {
   val failures = errors()
   clear()
   return failures.toAssertionError(depth, subject)
}

package io.kotest.assertions

import io.kotest.mpp.stacktraces
import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
actual val errorCollector: ErrorCollector = NoopErrorCollector

actual fun ErrorCollector.collectiveError(): AssertionError? {
   val failures = errors()
   clear()
   return failures.toAssertionError()
}

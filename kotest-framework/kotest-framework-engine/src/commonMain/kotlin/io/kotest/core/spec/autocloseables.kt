package io.kotest.core.spec

import io.kotest.engine.spec.AutoCloseable
import io.kotest.engine.spec.TestSuite
import io.kotest.engine.spec.autoClose

@Deprecated(
   "Moved to io.kotest.engine.spec. Will be removed in 4.4",
   ReplaceWith("this.autoClose(closeable)", "io.kotest.engine.spec.autoClose")
)
fun <T : AutoCloseable> TestSuite.autoClose(closeable: T): T = this.autoClose(closeable)

@Deprecated(
   "Moved to io.kotest.engine.spec. Will be removed in 4.4",
   ReplaceWith("this.autoClose(closeable)", "io.kotest.engine.spec.autoClose")
)
fun <T : AutoCloseable> TestSuite.autoClose(closeable: Lazy<T>): Lazy<T> = this.autoClose(closeable)

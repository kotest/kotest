package io.kotest.assertions

import io.kotest.assertions.print.Printed

actual val errorCollector: ErrorCollector = NoopErrorCollector

object NoopErrorCollector : ErrorCollector {
   override var depth = 0
   override var subject: Printed? = null

   override fun getCollectionMode(): ErrorCollectionMode = ErrorCollectionMode.Hard

   override fun setCollectionMode(mode: ErrorCollectionMode) {
   }

   override fun errors(): List<Throwable> = emptyList()

   override fun pushError(t: Throwable) {
   }

   override fun clear() {
   }

   override fun pushClue(clue: Clue) {
   }

   override fun popClue() {
   }

   override fun clueContext(): List<Clue> = emptyList()
}

actual fun ErrorCollector.collectiveError(): AssertionError? {
   val failures = errors()
   clear()
   return failures.toAssertionError(depth, subject)
}

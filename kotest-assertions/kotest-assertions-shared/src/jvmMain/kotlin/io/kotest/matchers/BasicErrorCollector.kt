package io.kotest.matchers

import io.kotest.assertions.print.Printed

open class BasicErrorCollector : ErrorCollector {

   protected val failures = mutableListOf<Throwable>()
   protected var mode = ErrorCollectionMode.Hard
   protected val clues = mutableListOf<Clue>()

   override var subject: Printed? = null
   override var depth: Int = 0

   override fun getCollectionMode(): ErrorCollectionMode = mode

   override fun setCollectionMode(mode: ErrorCollectionMode) {
      this.mode = mode
   }

   override fun pushClue(clue: Clue) {
      clues.add(0, clue)
   }

   override fun popClue() {
      clues.removeAt(0)
   }

   override fun clueContext(): List<Clue> = clues.reversed()

   override fun pushError(t: Throwable) {
      failures.add(t)
   }

   override fun errors(): List<Throwable> = failures.toList()

   override fun clear() = failures.clear()
}

package io.kotest.assertions

actual object ErrorCollector {

  private val failures = mutableListOf<Throwable>()
  private var mode = ErrorCollectionMode.Hard
  private val clues = mutableListOf<Any>()

  actual fun getCollectionMode(): ErrorCollectionMode = mode
  actual fun setCollectionMode(mode: ErrorCollectionMode) {
    ErrorCollector.mode = mode
  }

  actual fun pushClue(clue: Any) {
    clues.add(0, clue)
  }

  actual fun popClue() {
    clues.removeAt(0)
  }

  actual fun clueContext(): List<Any> = clues.toList()

  actual fun pushError(t: Throwable) {
    failures.add(t)
  }

  actual fun errors(): List<Throwable> = failures.toList()
  actual fun clear() = failures.clear()
}

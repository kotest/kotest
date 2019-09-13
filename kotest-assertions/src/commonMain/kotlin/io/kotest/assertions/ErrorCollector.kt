package io.kotest.assertions

enum class ErrorCollectionMode {
  Soft, Hard
}

expect object ErrorCollector {

  fun getCollectionMode(): ErrorCollectionMode

  fun setCollectionMode(mode: ErrorCollectionMode)

  /**
   * Returns the errors accumulated in the current context.
   */
  fun errors(): List<Throwable>

  /**
   * Adds the given error to the current context.
   */
  fun pushError(t: Throwable)

  /**
   * Clears all errors from the current context.
   */
  fun clear()

  fun pushClue(clue: Any)

  fun popClue()

  /**
   * Returns the current clue context.
   * That is all the clues nested to this point.
   */
  fun clueContext(): List<Any>
}

fun clueContextAsString() = ErrorCollector.clueContext().let {
  if (it.isEmpty()) "" else it.joinToString("\n", postfix = "\n")
}

/**
 * If we are in "soft assertion mode" will add this throwable to the
 * list of throwables for the current execution. Otherwise will
 * throw immediately.
 */
fun ErrorCollector.collectOrThrow(error: Throwable) {
  when (getCollectionMode()) {
    ErrorCollectionMode.Soft -> pushError(error)
    ErrorCollectionMode.Hard -> throw error
  }
}

/**
 * The errors for the current execution are thrown as a single
 * throwable.
 */
fun ErrorCollector.throwCollectedErrors() {
  // set the collection mode back to the default
  setCollectionMode(ErrorCollectionMode.Hard)
  val failures = errors()
  clear()
  if (failures.isNotEmpty()) {
    val t = if (failures.size == 1) failures[0] else MultiAssertionError(failures)
    Failures.clean(t)
    throw t
  }
}

package io.kotest.assertions

import io.kotest.mpp.stacktraces

expect val errorCollector: ErrorCollector

/**
 * Specifies an assertion mode:
 * - Hard: assertion errors are thrown immediately
 * - Soft: assertion errors are collected and throw together
 */
enum class ErrorCollectionMode {
   Soft, Hard
}

typealias Clue = () -> String

interface ErrorCollector {

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

   fun pushClue(clue: Clue)

   fun popClue()

   /**
    * Returns the current clue context.
    * That is all the clues nested to this point.
    */
   fun clueContext(): List<Clue>
}

open class BasicErrorCollector : ErrorCollector {

   private val failures = mutableListOf<Throwable>()
   private var mode = ErrorCollectionMode.Hard
   private val clues = mutableListOf<Clue>()

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

   override fun clueContext(): List<Clue> = clues.toList()

   override fun pushError(t: Throwable) {
      failures.add(t)
   }

   override fun errors(): List<Throwable> = failures.toList()

   override fun clear() = failures.clear()
}

fun clueContextAsString() = errorCollector.clueContext().let {
   if (it.isEmpty()) "" else it.joinToString("\n", postfix = "\n") { f -> f.invoke() }
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
 * All errors currently collected in the [ErrorCollector] are throw as a single [MultiAssertionError].
 */
fun ErrorCollector.throwCollectedErrors() {
   val failures = errors()
   clear()
   if (failures.isNotEmpty()) {
      val t = if (failures.size == 1) failures[0] else MultiAssertionError(failures)
      stacktraces.cleanStackTrace(t)
      throw t
   }
}

package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.common.stacktrace.stacktraces

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
   var depth: Int
   var subject: Printed?

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

internal fun ErrorCollector.getAndReplace(errors: Collection<Throwable>): List<Throwable> {
   val old = errors()

   clear()
   errors.forEach { pushError(it) }

   return old
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
   val cleanedError = stacktraces.cleanStackTrace(error)
   when (getCollectionMode()) {
      ErrorCollectionMode.Soft -> pushError(cleanedError)
      ErrorCollectionMode.Hard -> throw cleanedError
   }
}

fun ErrorCollector.pushErrors(errors: Collection<Throwable>) {
   errors.forEach {
      pushError(it)
   }
}

fun ErrorCollector.collectOrThrow(errors: Collection<Throwable>) {
   pushErrors(errors)

   if (getCollectionMode() == ErrorCollectionMode.Hard) {
      throwCollectedErrors()
   }
}

/**
 * All errors currently collected in the [ErrorCollector] are throw as a single [MultiAssertionError].
 */
fun ErrorCollector.throwCollectedErrors() {
   collectiveError()?.let { throw it }
}

/**
 * All errors currently collected in the [ErrorCollector] are returned as a single [MultiAssertionError].
 */
expect fun ErrorCollector.collectiveError(): AssertionError?

inline fun <reified T> ErrorCollector.runWithMode(mode: ErrorCollectionMode, block: () -> T): T =
   getCollectionMode().let { original ->
      setCollectionMode(mode)
      try {
         block()
      } finally {
         setCollectionMode(original)
      }
   }

internal fun List<Throwable>.toAssertionError(depth: Int, subject: Printed?): AssertionError? {
   return when {
      isEmpty() -> null
      size == 1 && subject != null -> AssertionError(createMessage(this, depth, subject))
      size == 1 && subject == null -> AssertionError(this[0].message)
      else -> MultiAssertionError(createMessage(this, depth, subject))
   }?.let {
      stacktraces.cleanStackTrace(it)
   }
}

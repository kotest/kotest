package io.kotest.matchers

import io.kotest.assertions.print.Printed

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

/**
 * Returns the current clue context as a string.
 * If there are no clues, returns an empty string.
 */
fun clueContextAsString(): String = errorCollector.clueContext().let {
   if (it.isEmpty()) "" else it.joinToString("\n", postfix = "\n") { f -> f.invoke() }
}

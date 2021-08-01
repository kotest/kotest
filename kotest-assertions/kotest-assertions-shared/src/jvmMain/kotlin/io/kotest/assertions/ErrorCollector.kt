@file:JvmName("jvmerrorcollector")

package io.kotest.assertions

import java.util.Stack

actual val errorCollector: ErrorCollector = ThreadLocalErrorCollector

object ThreadLocalErrorCollector : ErrorCollector {

   private val clueContext = object : ThreadLocal<Stack<Clue>>() {
      override fun initialValue(): Stack<Clue> = Stack()
   }

   private val failures = object : ThreadLocal<MutableList<Throwable>>() {
      override fun initialValue(): MutableList<Throwable> = mutableListOf()
   }

   private val collectionMode = object : ThreadLocal<ErrorCollectionMode>() {
      override fun initialValue() = ErrorCollectionMode.Hard
   }

   override fun setCollectionMode(mode: ErrorCollectionMode) = collectionMode.set(mode)

   override fun getCollectionMode(): ErrorCollectionMode = collectionMode.get()

   override fun pushClue(clue: Clue) {
      clueContext.get().push(clue)
   }

   override fun popClue() {
      clueContext.get().pop()
   }

   override fun clueContext(): List<Clue> = clueContext.get()

   override fun errors(): List<Throwable> = failures.get().toList()

   /**
    * Adds the given error to the current context.
    */
   override fun pushError(t: Throwable) {
      failures.get().add(t)
   }

   /**
    * Clears all errors from the current context.
    */
   override fun clear() = failures.get().clear()
}

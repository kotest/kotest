package io.kotest.assertions

import io.kotest.matchers.ErrorCollectionMode
import io.kotest.matchers.errorCollector

inline fun <T> assertSoftly(assertions: () -> T): T {
   // Handle the edge case of nested calls to this function by only calling throwCollectedErrors in the
   // outermost verifyAll block
   if (errorCollector.getCollectionMode() == ErrorCollectionMode.Soft) {
      val oldErrors = errorCollector.errors()
      errorCollector.clear()
      errorCollector.depth++

      return try {
         assertions()
      } catch (ex: Exception) {
         errorCollector.pushError(ex)
         throw ex
      } finally {
         val aggregated = errorCollector.collectErrors()
         errorCollector.clear()
         errorCollector.pushErrors(oldErrors)
         aggregated?.let { errorCollector.pushError(it) }
         errorCollector.depth--
      }
   }

   errorCollector.setCollectionMode(ErrorCollectionMode.Soft)
   return try {
      assertions()
   } catch (ex: Exception) {
      errorCollector.pushError(ex)
      throw ex
   } finally {
      // In case if any exception is thrown from assertions block setting errorCollectionMode back to hard
      // so that it won't remain soft for others tests. See https://github.com/kotest/kotest/issues/1932
      errorCollector.setCollectionMode(ErrorCollectionMode.Hard)
      errorCollector.throwCollectedErrors()
   }
}

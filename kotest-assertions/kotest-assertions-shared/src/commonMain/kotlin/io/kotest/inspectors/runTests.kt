package io.kotest.inspectors

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector

inline fun <T> runTests(col: Collection<T>, f: (T) -> Unit): List<ElementResult<T>> {
   return col.map {
      val originalAssertionMode = errorCollector.getCollectionMode()
      try {
         errorCollector.setCollectionMode(ErrorCollectionMode.Hard)
         f(it)
         ElementPass(it)
      } catch (e: Throwable) {
         ElementFail(it, e)
      } finally {
         errorCollector.setCollectionMode(originalAssertionMode)
      }
   }
}

package io.kotest.inspectors

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector

inline fun <T> runTests(col: Collection<T>, f: (T) -> Unit): List<ElementResult<T>> {
   return col.map {
      runTest(it, f)
   }
}

inline fun <K, V, T : Map.Entry<K, V>> runTests(
   map: Map<K, V>,
   f: (Map.Entry<K, V>) -> Unit
): List<ElementResult<Map.Entry<K, V>>> {
   return map.entries.map {
      runTest(it, f)
   }
}

inline fun <T> runTest(t: T, f: (T) -> Unit): ElementResult<T> {
   val originalAssertionMode = errorCollector.getCollectionMode()
   return try {
      errorCollector.setCollectionMode(ErrorCollectionMode.Hard)
      f(t)
      ElementPass(t)
   } catch (e: Throwable) {
      ElementFail(t, e)
   } finally {
      errorCollector.setCollectionMode(originalAssertionMode)
   }
}

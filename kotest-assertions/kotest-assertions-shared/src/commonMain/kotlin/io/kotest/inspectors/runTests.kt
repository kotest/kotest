package io.kotest.inspectors

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector

@PublishedApi
internal inline fun <T> runTests(col: Collection<T>, f: (T) -> Unit): List<ElementResult<T>> {
   return col.mapIndexed { index, it ->
      runTest(index, it, f)
   }
}

@PublishedApi
internal inline fun <K, V, T : Map.Entry<K, V>> runTests(
   map: Map<K, V>,
   f: (Map.Entry<K, V>) -> Unit
): List<ElementResult<Map.Entry<K, V>>> {
   return map.entries.mapIndexed { index, it ->
      runTest(index, it, f)
   }
}

@PublishedApi
internal inline fun <T> runTest(index: Int, t: T, f: (T) -> Unit): ElementResult<T> {
   val originalAssertionMode = errorCollector.getCollectionMode()
   return try {
      errorCollector.setCollectionMode(ErrorCollectionMode.Hard)
      f(t)
      ElementPass(index, t)
   } catch (e: Throwable) {
      ElementFail(index, t, e)
   } finally {
      errorCollector.setCollectionMode(originalAssertionMode)
   }
}

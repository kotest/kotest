package io.kotest.matchers.collections

import io.kotest.assertions.stringRepr

internal object CollectionMatchersConstants {
   const val maxSnippetSize = 10
}

internal fun <TValue> Iterable<TValue>.getCollectionSnippet(): String {
   return joinToString(separator = ",", limit = CollectionMatchersConstants.maxSnippetSize) {
      stringRepr(it)
   }
}

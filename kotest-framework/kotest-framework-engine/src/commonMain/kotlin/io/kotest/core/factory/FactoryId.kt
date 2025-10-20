package io.kotest.core.factory

import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

data class FactoryId(val value: String) {
   @OptIn(ExperimentalAtomicApi::class)
   companion object {
      val counter = AtomicLong(1)
      // Kotlin uuids are broken in kotlin currently for some versions of node (they use a javascript method not available on all versions)
      // we don't actually care that id value is here, as long as its unique, so a counter will do just as well
      // we can switch to a uuid later if/when they come out of experimental mode
      // see https://github.com/kotest/kotest/issues/5109
      fun next(): FactoryId = FactoryId("factory-id-" + counter.incrementAndFetch().toString())
   }
}

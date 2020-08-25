package io.kotest.core.factory

import io.kotest.mpp.uniqueId

data class FactoryId(val value: String) {
   companion object {
      fun next(): FactoryId = FactoryId(uniqueId())
   }
}

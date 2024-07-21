package io.kotest.core.factory

data class FactoryId(val value: String) {
   companion object {
      fun next(): FactoryId = FactoryId(uniqueId())
   }
}

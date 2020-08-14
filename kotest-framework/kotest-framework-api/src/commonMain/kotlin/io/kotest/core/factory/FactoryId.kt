package io.kotest.core.factory

data class FactoryId(val value: Int) {
   companion object {
      private var counter = 0
      fun next(): FactoryId = FactoryId(counter++)
   }
}

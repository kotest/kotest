package io.kotest.core.factory

data class TestFactoryId(val value: Int) {
   companion object {
      private var counter = 0
      fun next(): TestFactoryId = TestFactoryId(counter++)
   }
}

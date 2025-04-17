package io.kotest.core.factory

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class FactoryId(val value: String) {
   companion object {
      @OptIn(ExperimentalUuidApi::class)
      fun next(): FactoryId = FactoryId(Uuid.random().toString())
   }
}

package io.kotest.property.core.seeds

import io.kotest.common.TestPathContextElement
import io.kotest.property.PropertyTesting
import io.kotest.property.RandomSource
import io.kotest.property.core.PermutationConfiguration
import io.kotest.property.random
import io.kotest.property.seed.readSeed
import io.kotest.property.seed.writeSeed
import kotlinx.coroutines.currentCoroutineContext

internal suspend fun createRandomSource(configuration: PermutationConfiguration): RandomSource {
   return configuration.seed?.random() ?: getFailedSeed()?.random() ?: RandomSource.default()
}

internal suspend fun getFailedSeed(): Long? {
   if (!PropertyTesting.writeFailedSeed) return null
   val path = currentCoroutineContext()[TestPathContextElement]?.testPath ?: return null
   return readSeed(path)
}

suspend fun writeFailedSeed(seed: Long) {
   if (PropertyTesting.writeFailedSeed) {
      val path = currentCoroutineContext()[TestPathContextElement]?.testPath ?: return
      writeSeed(path, seed)
   }
}

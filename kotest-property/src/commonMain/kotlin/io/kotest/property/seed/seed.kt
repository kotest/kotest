package io.kotest.property.seed

import io.kotest.common.TestPath
import io.kotest.common.TestPathContextElement
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.RandomSource
import io.kotest.property.random
import kotlinx.coroutines.currentCoroutineContext

internal suspend fun createRandom(config: PropTestConfig): RandomSource {
   return config.seed?.random() ?: getFailedSeed()?.random() ?: RandomSource.default()
}

internal suspend fun getFailedSeed(): Long? {
   if (!PropertyTesting.writeFailedSeed) return null
   val path = currentCoroutineContext()[TestPathContextElement]?.testPath ?: return null
   return readSeed(path)
}

internal suspend fun writeFailedSeed(seed: Long) {
   if (PropertyTesting.writeFailedSeed) {
      val path = currentCoroutineContext()[TestPathContextElement]?.testPath ?: return
      writeSeed(path, seed)
   }
}

internal suspend fun clearFailedSeed() {
   val path = currentCoroutineContext()[TestPathContextElement]?.testPath ?: return
   clearSeed(path)
}

internal expect fun readSeed(path: TestPath): Long?

internal expect fun writeSeed(path: TestPath, seed: Long)

internal expect fun clearSeed(path: TestPath)

package io.kotest.property.seed

import io.kotest.common.ExperimentalKotest
import io.kotest.common.TestPath
import io.kotest.common.TestPathContextElement
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.RandomSource
import io.kotest.property.random
import kotlinx.coroutines.currentCoroutineContext

@ExperimentalKotest
suspend fun createRandom(config: PropTestConfig): RandomSource {
   return config.seed?.random() ?: getFailedSeedIfEnabled()?.random() ?: RandomSource.default()
}

@ExperimentalKotest
suspend fun getFailedSeedIfEnabled(): Long? {
   return if (PropertyTesting.writeFailedSeed) getFailedSeed() else null
}

@ExperimentalKotest
suspend fun getFailedSeed(): Long? {
   val path = currentCoroutineContext()[TestPathContextElement]?.testPath ?: return null
   return readSeed(path)
}

@ExperimentalKotest
suspend fun writeFailedSeedIfEnabled(seed: Long) {
   if (PropertyTesting.writeFailedSeed) {
      writeFailedSeed(seed)
   }
}

@ExperimentalKotest
suspend fun writeFailedSeed(seed: Long) {
   val path = currentCoroutineContext()[TestPathContextElement]?.testPath ?: return
   writeSeed(path, seed)
}

@ExperimentalKotest
suspend fun clearFailedSeed() {
   val path = currentCoroutineContext()[TestPathContextElement]?.testPath ?: return
   clearSeed(path)
}

internal expect fun readSeed(path: TestPath): Long?

internal expect fun writeSeed(path: TestPath, seed: Long)

internal expect fun clearSeed(path: TestPath)

internal expect suspend fun cleanUpSeedFiles()

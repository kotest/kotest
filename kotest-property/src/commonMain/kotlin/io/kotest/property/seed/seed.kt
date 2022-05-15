package io.kotest.property.seed

import io.kotest.common.ExperimentalKotest
import io.kotest.framework.shared.test.TestPath
import io.kotest.framework.shared.test.TestPathContextElement
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.RandomSource
import io.kotest.property.random
import kotlinx.coroutines.currentCoroutineContext

@ExperimentalKotest
suspend fun createRandom(config: PropTestConfig): RandomSource {
   return getFailedSeedIfEnabled()?.random() ?: config.seed?.random() ?: RandomSource.default()
}

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
   if (PropertyTesting.writeFailedSeed)
      writeFailedSeed(seed)
}

@ExperimentalKotest
suspend fun writeFailedSeed(seed: Long) {
   val path = currentCoroutineContext()[TestPathContextElement]?.testPath ?: return
   writeSeed(path, seed)
}

expect fun readSeed(path: TestPath): Long?
expect fun writeSeed(path: TestPath, seed: Long)

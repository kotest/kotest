package io.kotest.property.seed

import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.descriptors.DescriptorPathContextElement
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
   val path = currentCoroutineContext()[DescriptorPathContextElement]?.path ?: return null
   return readSeed(path)
}

suspend fun writeFailedSeed(seed: Long) {
   if (PropertyTesting.writeFailedSeed) {
      val path = currentCoroutineContext()[DescriptorPathContextElement]?.path ?: return
      writeSeed(path, seed)
   }
}

internal suspend fun clearFailedSeed() {
   val path = currentCoroutineContext()[DescriptorPathContextElement]?.path ?: return
   clearSeed(path)
}

internal expect fun readSeed(path: DescriptorPath): Long?

internal expect fun writeSeed(path: DescriptorPath, seed: Long)

internal expect fun clearSeed(path: DescriptorPath)

package io.kotest.permutations.seeds

import io.kotest.common.DescriptorPath
import io.kotest.common.DescriptorPathContextElement
import io.kotest.permutations.PermutationConfiguration
import io.kotest.property.PropertyTesting
import io.kotest.property.RandomSource
import io.kotest.property.random
import kotlinx.coroutines.currentCoroutineContext

internal object SeedOperations {

   suspend fun createRandomSource(configuration: PermutationConfiguration): RandomSource {
      return configuration.seed?.random() ?: getFailedSeed()?.random() ?: RandomSource.default()
   }

   private suspend fun getFailedSeed(): Long? {
      if (!PropertyTesting.writeFailedSeed) return null
      val path = currentCoroutineContext()[DescriptorPathContextElement]?.path ?: return null
      return readSeed(path)
   }

   /**
    * Writes the seed to the test output if [PropertyTesting.writeFailedSeed] is enabled.
    */
   suspend fun writeFailedSeed(writeFailedSeed: Boolean, seed: Long) {
      if (writeFailedSeed) {
         val path = currentCoroutineContext()[DescriptorPathContextElement]?.path ?: return
         writeSeed(path, seed)
      }
   }

   suspend fun clearFailedSeed() {
      val path = currentCoroutineContext()[DescriptorPathContextElement]?.path ?: return
      clearSeed(path)
   }
}

internal expect fun readSeed(path: DescriptorPath): Long?

internal expect fun writeSeed(path: DescriptorPath, seed: Long)

internal expect fun clearSeed(path: DescriptorPath)

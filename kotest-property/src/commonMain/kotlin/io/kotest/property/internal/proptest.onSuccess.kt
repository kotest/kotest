package io.kotest.property.internal

import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.checkMaxDiscards
import io.kotest.property.classifications.outputClassifications
import io.kotest.property.seed.clearFailedSeed
import io.kotest.property.statistics.outputStatistics


internal suspend fun PropertyContext.onSuccess(
   args: Int,
   random: RandomSource,
) {
   outputStatistics(this, args, true)
   outputClassifications(args, config, random.seed)
   checkMinSuccess(config, random.seed)
   if (args > 1) checkMaxDiscards()
   clearFailedSeed()
}

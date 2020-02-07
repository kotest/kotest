package io.kotest.property.arbitrary

import io.kotest.property.Sample
import kotlin.random.Random

fun Arb.Companion.bool() = object : Arb<Boolean> {
   override fun edgecases(): List<Boolean> = listOf(true,false)
   override fun sample(random: Random): Sample<Boolean> = Sample(random.nextBoolean())
}

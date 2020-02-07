package io.kotest.property.arbitrary

import io.kotest.property.RandomSource
import io.kotest.property.Sample

fun Arb.Companion.bool() = object : Arb<Boolean> {
   override fun edgecases(): List<Boolean> = listOf(true,false)
   override fun sample(rs: RandomSource): Sample<Boolean> = Sample(rs.random.nextBoolean())
}

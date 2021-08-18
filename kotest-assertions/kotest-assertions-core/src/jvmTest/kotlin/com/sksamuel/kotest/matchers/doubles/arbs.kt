package com.sksamuel.kotest.matchers.doubles

import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.float

val nonNumericDoubles = listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)
val numericDoubles = Arb.double().filterNot { it in nonNumericDoubles }
val nonMinNorMaxValueDoubles = numericDoubles.filterNot { it in listOf(Double.MAX_VALUE, Double.MIN_VALUE) }


val nonNumericFloats = listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)
val numericFloats = Arb.float().filterNot { it in nonNumericFloats }

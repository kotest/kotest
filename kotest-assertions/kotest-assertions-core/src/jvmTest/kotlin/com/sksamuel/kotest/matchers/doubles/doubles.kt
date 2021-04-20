package com.sksamuel.kotest.matchers.doubles

import kotlin.math.ulp

fun Double.toleranceValue(): Double = ulp
fun Double.slightlyGreater(): Double = this + (2 * ulp)
fun Double.muchGreater(): Double = this + (3 * ulp)
fun Double.slightlySmaller(): Double = this - (2 * ulp)
fun Double.muchSmaller(): Double = this - (3 * ulp)


package io.kotest.property

import kotlin.random.Random

fun Long.random() = when (this) {
   0L -> Random.Default
   else -> Random(this)
}

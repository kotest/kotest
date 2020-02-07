package io.kotest.property.arbitrary

import com.mifmif.common.regex.Generex

fun Arb.Companion.regex(regex: String) = arb {
   val generex = Generex(regex)
   generex.setSeed(it.seed)
   generex.random()
}

fun Arb.Companion.regex(regex: Regex) = regex(regex.pattern)

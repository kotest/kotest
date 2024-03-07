package io.kotest.property.statistics

import io.kotest.common.ExperimentalKotest

@ExperimentalKotest
data class Label(val value: String)

@ExperimentalKotest
data class Statistics(
   val iterations: Int,
   val args: Int,
   val labels: Set<Label>,
   val statistics: Map<Label?, Map<Any?, Int>>,
   val success: Boolean,
)

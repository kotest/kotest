package io.kotest.property.statistics

data class Label(val value: String)

data class Statistics(
   val iterations: Int,
   val args: Int,
   val labels: Set<Label>,
   val statistics: Map<Label?, Map<Any?, Int>>,
   val success: Boolean,
)

package io.kotest.property.statistics

/**
 * A label is a string that can be used to group together classifications.
 *
 * For example, you may wish to classify a value by even and odd, and also by positive and negative.
 * To do this, you could use two labels: "parity" which would track the even or oddness of the value, and
 * another label "sign" which tracks if positive or negative.
 */
data class Label(val value: String)

data class Statistics(
   val iterations: Int,
   val args: Int,
   val labels: Set<Label>,
   val statistics: Map<Label?, Map<Any?, Int>>,
   val success: Boolean,
)

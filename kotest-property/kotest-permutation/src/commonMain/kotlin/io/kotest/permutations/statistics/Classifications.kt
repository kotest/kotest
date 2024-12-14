package io.kotest.permutations.statistics

import io.kotest.property.statistics.Label

class Classifications(
   val counts: MutableMap<Label?, MutableMap<Any?, Int>> = mutableMapOf()
)

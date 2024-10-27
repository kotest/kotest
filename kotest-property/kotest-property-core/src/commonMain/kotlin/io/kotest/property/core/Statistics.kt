package io.kotest.property.core

import io.kotest.property.statistics.Label

class Statistics(
   val statistics: MutableMap<Label, Any> = mutableMapOf()
)

package io.kotest.property.shrinker

import io.kotest.property.PropertyInput

class MapShrinker<K, V> : Shrinker<Map<K, V>> {
   override fun shrink(value: Map<K, V>): List<PropertyInput<Map<K, V>>> {
      return when (value.size) {
         0 -> emptyList()
         1 -> listOf(PropertyInput(emptyMap()))
         else -> listOf(
            PropertyInput(value.toList().take(value.size / 2).toMap(), this),
            PropertyInput(value.toList().drop(1).toMap(), this)
         )
      }
   }
}

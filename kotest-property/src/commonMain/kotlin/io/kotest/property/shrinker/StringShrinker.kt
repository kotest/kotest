package io.kotest.property.shrinker

import io.kotest.property.PropertyInput

object StringShrinker : Shrinker<String> {
   override fun shrink(value: String): List<PropertyInput<String>> = when (value.length) {
      0 -> emptyList()
      1 -> listOf(PropertyInput(""), PropertyInput("a"))
      else -> {
         val first = value.take(value.length / 2 + value.length % 2)
         val second = value.takeLast(value.length / 2)
         // always include empty string as the best io.kotest.properties.shrinking.shrink
         listOf("", first, first.padEnd(value.length, 'a'), second, second.padStart(value.length, 'a'))
            .map { PropertyInput(it, StringShrinker) }
      }
   }
}

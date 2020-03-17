package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

inline fun <reified T : Enum<T>> Exhaustive.Companion.enum(): Exhaustive<T> {
   val constants = enumValues<T>().asList()
   return exhaustive(constants)
}

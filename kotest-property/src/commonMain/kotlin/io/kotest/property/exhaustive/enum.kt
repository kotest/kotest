package io.kotest.property.exhaustive

inline fun <reified T : Enum<T>> Exhaustive.Companion.enum(): Exhaustive<T> {
   val constants = enumValues<T>().asList()
   return exhaustive(constants)
}

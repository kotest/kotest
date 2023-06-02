package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

inline fun <reified T : Enum<T>> Exhaustive.Companion.enum(): Exhaustive<T> {
   val constants = enumValues<T>().asList()
   return exhaustive(constants)
}

inline fun <reified T : Enum<T>> Exhaustive.Companion.enumsExcept(vararg elements: T): Exhaustive<T> {
   val constants = enumValues<T>().toMutableList().apply { removeAll(elements.toSet()) }
   return exhaustive(constants)
}

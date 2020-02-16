package io.kotest.property.arbitrary

inline fun <reified T : Enum<T>> Arb.Companion.enum(): Arb<T> {
   val constants = enumValues<T>().asList()
   return arb { constants.shuffled(it.random).first() }
}

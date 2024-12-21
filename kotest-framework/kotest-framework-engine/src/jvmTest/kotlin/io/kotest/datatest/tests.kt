package io.kotest.datatest

data class PythagTriple(val a: Int, val b: Int, val c: Int)

internal data class FruitWithMemberNameCollision(
   val name: String,
   val weight: Int
) {
   fun weight() = 42
}

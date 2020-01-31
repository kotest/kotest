package io.kotest.property

import kotlin.random.Random

interface Foo<T> {
   fun element(): T
}

fun <T> Foo<T>.take(k: Int): Bar<T> = object : io.kotest.property.Bar<T> {
   override fun seq(): kotlin.sequences.Sequence<T> =
      kotlin.sequences.sequence { kotlin.repeat(k) { yield(this@take.element()) } }
}

interface Bar<T> {
   fun seq(): Sequence<T>
}

object Foos {
   fun ints(): Foo<Int> = object : Foo<Int> {
      override fun element(): Int = Random.nextInt()
   }

   fun pos(): Foo<Int> = object : Foo<Int> {
      override fun element(): Int = kotlin.math.abs(Random.nextInt(0, 10))
      fun Foo<Int>.take(k: Int) = object : Bar<Int> {
         override fun seq(): Sequence<Int> = sequence { repeat(3) { yield(this@take.element()) } }
      }
   }

   fun strings(): Foo<String> = object : Foo<String> {
      override fun element(): String = "a"
      fun Foo<String>.take(k: Int) = object : Bar<String> {
         override fun seq(): Sequence<String> = sequence { repeat(3) { yield(this@take.element()) } }
      }
   }
}

fun main() {
   println(Foos.ints().take(10).seq().toList())
   println(Foos.pos().take(10).seq().toList())
   println(Foos.strings().take(10).seq().toList())
}


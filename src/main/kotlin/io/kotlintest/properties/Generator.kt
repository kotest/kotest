package io.kotlintest.properties

import com.sksamuel.koors.Random

interface Generator<T> {
  fun generate(): T

  companion object {
    fun choose(min: Int, max: Int): Generator<Int> = object : Generator<Int> {
      override fun generate(): Int = Random.default.nextInt(max - min) + min
    }

    fun <T> oneOf(values: List<T>): Generator<T> = object : Generator<T> {
      override fun generate(): T = Random.default.shuffle(values).first()
    }

    fun string(): Generator<String> = object : Generator<String> {
      override fun generate(): String = nextPrintableString(Random.default.nextInt(100))
    }
  }

  fun nextString(length: Int): String {
    fun safeChar(): Char {
      val surrogateStart: Int = 0xD800
      val res = Random.default.nextInt(surrogateStart - 1) + 1
      return res.toChar()
    }
    return (0..length).map { safeChar() }.joinToString("")
  }

  fun nextPrintableString(length: Int): String {
    return (0..length).map { Random.Companion.default.nextPrintableChar() }.joinToString("")
  }
}
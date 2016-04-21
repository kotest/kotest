package io.kotlintest

import io.kotlintest.specs.WordSpec

class Table<T>(vararg val lists: List<T>)

class TableChecksTest : WordSpec() {

  fun fibo(k: Int): Int = when (k) {
    0 -> 0
    1 -> 1
    else -> fibo(k - 1) + fibo(k - 2)
  }

  init {
    "fibo" should {
      "return correct value" {

        val inputs = Table(
            listOf(0, 1),
            listOf(1, 1),
            listOf(2, 2),
            listOf(3, 3),
            listOf(4, 5)
        )

        //  forAll(inputs, {})

      }
    }
  }
}


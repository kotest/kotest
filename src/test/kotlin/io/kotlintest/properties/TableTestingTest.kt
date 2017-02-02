package io.kotlintest.properties

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class TableTestingTest : StringSpec() {
  init {

    "names should not be empty strings" {

      val table1 = table(
          headers("name"),
          row("sam"),
          row("billy"),
          row("christian")
      )

      forAll(table1) {
        it.isEmpty() shouldBe false
      }
    }

    "numbers should add up to ten" {

      val table2 = table(
          headers("a", "b"),
          row(5, 5),
          row(4, 6),
          row(3, 7)
      )

      forAll(table2) { a, b ->
        a + b == 10
      }
    }

    "numbers should be py triples" {

      val table3 = table(
          headers("x", "y", "z"),
          row(3, 4, 5),
          row(5, 12, 13),
          row(9, 12, 15)
      )

      forAll(table3) { a, b, c ->
        a * a + b * b shouldBe c * c
      }
    }

    "testing triple concat" {
      val table4 = table(
          headers("a", "b", "c", "d"),
          row("sam", "bam", "dam", "sambamdam"),
          row("", "sam", "", "sam"),
          row("sa", "", "m", "sam")
      )
      forAll(table4) { a, b, c, d ->
        a + b + c shouldBe d
      }
    }

    "should use table with maximum columns" {
      val table5 = table(
          headers("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "result"),
          row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 231),
          row(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 21)
      )
      forAll(table5) { a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, result ->
        a + b + c + d + e + f + g + h + i + j + k + l + m + n + o + p + q + r + s + t + u  shouldBe result
      }
    }
  }
}

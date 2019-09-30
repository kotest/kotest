package com.sksamuel.kotest.tables

import io.kotest.assertions.MultiAssertionError
import io.kotest.assertions.fail
import io.kotest.matchers.string.contain
import io.kotest.matchers.types.shouldNotBeInstanceOf
import io.kotest.should
import io.kotest.shouldBe
import io.kotest.shouldNot
import io.kotest.shouldNotBe
import io.kotest.shouldThrow
import io.kotest.specs.FreeSpec
import io.kotest.specs.StringSpec
import io.kotest.tables.forAll
import io.kotest.tables.forNone
import io.kotest.tables.headers
import io.kotest.tables.row
import io.kotest.tables.table
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.kotlinFunction

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
        a + b shouldBe 10
      }
    }

    "numbers should add up to ten using extension function" {

      table(headers("a", "b"),
          row(5, 5),
          row(4, 6),
          row(3, 7)
      ).forAll { a, b ->
        a + b shouldBe 10
      }
    }

    "numbers should all be different using extension function" {

      table(headers("a", "b"),
          row(1, 2),
          row(3, 4),
          row(5, 6)
      ).forNone { a, b ->
        a shouldBe b
      }
    }

    "forNone should fail if any rows succeed" {
      shouldThrow<AssertionError> {
        table(headers("a", "b"),
            row(1, 1)
        ).forNone { a, b ->
          a shouldBe b
        }
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

      table3.forAll { a, b, c ->
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
        a + b + c + d + e + f + g + h + i + j + k + l + m + n + o + p + q + r + s + t + u shouldBe result
      }
    }

    "should be able to combine subtypes in one table" {
      abstract class Shape

      val circle = object : Shape() {}
      val square = object : Shape() {}

      table(
          headers("a", "b", "c"),
          row("foo", 5, circle),
          row("bar", 42, square)
      )
    }

    "rows should be executed after error" {
      var count = 0
      shouldThrow<AssertionError> {
        val table1 = table(
            headers("name"),
            row("sam"),
            row("billy"),
            row("christian")
        )

        forAll(table1) {
          count += 1
          it shouldNotBe "sam"
        }
      }

      count shouldBe 3
    }

    "assertions should be grouped in order" {
      shouldThrow<MultiAssertionError> {
        val table1 = table(
            headers("name"),
            row("sam"),
            row("billy"),
            row("christian")
        )

        forAll(table1) {
          it shouldBe "christian"
        }
      }.let {
        println(it.message)
        it.message shouldNotBe null
        it.message should contain("1) Test failed for (name, sam) with error expected: \"christian\" but was: \"sam\"")
        it.message should contain("2) Test failed for (name, billy) with error expected: \"christian\" but was: \"billy\"")
        it.message shouldNot contain("3) Test failed")
      }
    }

    "single failures should not be grouped" {
      shouldThrow<AssertionError> {
        val table1 = table(
            headers("name"),
            row("sam"),
            row("billy"),
            row("christian")
        )

        forAll(table1) {
          it shouldNotBe "christian"
        }
      }.let {
        it.shouldNotBeInstanceOf<MultiAssertionError>()
        it.message shouldBe "Test failed for (name, christian) with error \"christian\" should not equal \"christian\""
      }
    }

    "all exceptions should be grouped" {
      shouldThrow<AssertionError> {
        val table1 = table(
            headers("name"),
            row(null),
            row("billy"),
            row("christian")
        )

        forAll(table1) {
          it!! shouldNotBe "christian"
        }
      }.let {
        it.message should contain("1) Test failed for (name, null) with error kotlin.KotlinNullPointerException")
        it.message should contain("2) Test failed for (name, christian) with error \"christian\" should not equal \"christian\"")
        it.message shouldNot contain("3) Test failed")
      }
    }
  }
}

class TableTestingCoroutinesTests : FreeSpec() {

  init {
    "Should allow suspend execution" {
      allFunctionsThatTakeALambda().shouldHaveTheInlineModifier()
    }
  }

  private fun allFunctionsThatTakeALambda(): List<KFunction<*>> {
    return Class
            .forName("io.kotest.tables.TableTestingKt")
            .declaredMethods
            .map { it.kotlinFunction!! }
            .filter { fn -> fn.parameters.any { it.type.isSubtypeOf(Function::class.starProjectedType) } }
  }

  private fun List<KFunction<*>>.shouldHaveTheInlineModifier() {
    forEach {
      if (!it.isInline) {
        fail("$it should be inlined")
      }
    }
  }
}

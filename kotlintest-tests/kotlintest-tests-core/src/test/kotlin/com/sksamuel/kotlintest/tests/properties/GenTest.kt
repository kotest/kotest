@file:Suppress("USELESS_IS_CHECK")

package com.sksamuel.kotlintest.tests.properties

import io.kotlintest.*
import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.matchers.collections.contain
import io.kotlintest.matchers.gte
import io.kotlintest.matchers.lt
import io.kotlintest.matchers.string.include
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table
import java.util.Random
import kotlin.collections.ArrayList

class GenTest : WordSpec() {
  init {
    "Gen.string.nextPrintableString" should {
      "give out a argument long string".config(invocations = 100, threads = 8) {
        val random = Random()
        var rand = random.nextInt(10000)
        if (rand <= 0)
          rand = 0 - rand
        val string = Gen.string().nextPrintableString(rand)

        string.forEach {
          it.toInt() shouldBe gte(32)
          it.toInt() shouldBe lt(127)
        }
        string.length shouldBe rand
      }
    }
    "Gen.choose<int, int>" should {

      "only give out numbers in the given range".config(invocations = 10000, threads = 8) {
        val random = Random()

        val min = random.nextInt(10000) - 10000
        val max = random.nextInt(10000) + 10000

        val rand = Gen.choose(min, max).random().take(10)
        rand.forEach {
          it shouldBe gte(min)
          it shouldBe lt(max)
        }
      }

      "support negative bounds".config(invocations = 1000, threads = 8) {

        val random = Random()

        val max = random.nextInt(10000)

        val rand = Gen.choose(Int.MIN_VALUE, max).random().take(10)
        rand.forEach {
          it shouldBe gte(Int.MIN_VALUE)
          it shouldBe lt(max)
        }

      }
    }
    "Gen.choose<long, long>" should {
      "only give out numbers in the given range".config(invocations = 10000, threads = 8) {
        val random = Random()

        val min = random.nextInt(10000) - 10000
        val max = random.nextInt(10000) + 10000

        val rand = Gen.choose(min.toLong(), max.toLong()).random().take(10)
        rand.forEach {
          it shouldBe gte(min.toLong())
          it shouldBe lt(max.toLong())
        }

      }
      "support negative bounds".config(invocations = 10000, threads = 8) {
        val random = Random()

        val max = random.nextInt(10000) + 10000

        val rand = Gen.choose(Long.MIN_VALUE, max.toLong()).random().take(10)
        rand.forEach {
          it shouldBe gte(Long.MIN_VALUE)
          it shouldBe lt(max.toLong())
        }

      }
    }
    "Gen.forClassName" should {
      "gives the right result" {

        val table1 = table(
            headers("name"),
            row("java.lang.String"),
            row("java.lang.Integer"),
            row("java.lang.Long"),
            row("java.lang.Boolean"),
            row("java.lang.Float"),
            row("java.lang.Double")
        )

        io.kotlintest.tables.forAll(table1) { clazz ->
          Gen.forClassName(clazz).random().firstOrNull()!!.javaClass.name shouldBe clazz
        }

        val table2 = table(
            headers("name"),
            row("kotlin.String"),
            row("kotlin.Long"),
            row("kotlin.Boolean"),
            row("kotlin.Float"),
            row("kotlin.Double")
        )

        io.kotlintest.tables.forAll(table2) { clazz ->
          val tmp = clazz.split(".").last()
          Gen.forClassName(clazz).random().firstOrNull()!!.javaClass.name shouldBe "java.lang.$tmp"
        }

        Gen.forClassName("kotlin.Int").random().firstOrNull()!!.javaClass.name shouldBe "java.lang.Integer"
      }
      "throw an exception, with a wrong class" {
        shouldThrow<IllegalArgumentException> {
          Gen.forClassName("This.is.not.a.valid.class")
        }
      }
    }
    "Gen.create" should {
      "create a Generator with the given function" {
        Gen.create { 5 }.random().take(10).toList() shouldBe List(10, { 5 })
        var i = 0
        val gen = Gen.create { i++ }
        gen.random().take(150).toList() shouldBe List(150, { it })
      }
    }
    "Gen.default" should {
      "generate the defaults for list" {

        val gen = Gen.default<List<Int>>()
        io.kotlintest.properties.forAll(10, gen) { inst ->
          forAll(inst) { i ->
            (i is Int) shouldBe true
          }
          true
        }
      }

      "generate the defaults for set" {

        val gen = Gen.default<Set<String>>()
        io.kotlintest.properties.forAll(gen) { inst ->
          forAll(inst) { i ->
            (i is String) shouldBe true
          }
          true

        }

      }

      "use forClass for everything else" {

        val table = table(headers("name"),
            row("java.lang.String"),
            row("kotlin.String"),
            row("java.lang.Integer"),
            row("kotlin.Int"),
            row("java.lang.Long"),
            row("kotlin.Long"),
            row("java.lang.Boolean"),
            row("kotlin.Boolean"),
            row("java.lang.Float"),
            row("kotlin.Float"),
            row("java.lang.Double"),
            row("kotlin.Double"))

        io.kotlintest.tables.forAll(table) { clazz ->
          val tmp = clazz.split(".")
          Gen.forClassName(clazz).random().firstOrNull()!!.javaClass.name shouldHave include(tmp[tmp.size - 1])
        }
      }
      "throw an exeption, with a wrong class" {
        shouldThrow<IllegalArgumentException> {
          Gen.forClassName("This.is.not.a.valid.class")
        }
      }
    }

    "ConstGen " should {
      "always generate the same thing" {
        io.kotlintest.properties.forAll(Gen.constant(5)) {
          it == 5
        }
      }
    }

    "Gen.orNull " should {
      "have both values and nulls generated" {
        Gen.constant(5).orNull().constants().toSet() shouldBe setOf(5, null)

        fun <T> Gen<T>.toList(size: Int): List<T> =
            ArrayList<T>(size).also { list ->
              repeat(size) {
                list += random().take(10)
              }
            }

        Gen.constant(5).orNull().random().take(1000).toList().toSet() shouldBe setOf(5, null)
      }
    }

    "Gen.filter " should {
      "prevent values from being generated" {
        io.kotlintest.properties.forAll(Gen.from(listOf(1, 2, 5)).filter { it != 2 }) {
          it != 2
        }
      }
    }

    "Gen.map " should {
      "correctly transform the values" {
        io.kotlintest.properties.forAll(Gen.constant(5).map { it + 7 }) {
          it == 12
        }
      }
    }

    "Gen.from " should {
      "correctly handle null values" {
        val list = listOf(null, 1,2,3)
        val gen = Gen.from(list)
        val firstElements = gen.random().take(100).toList()
        firstElements shouldHave contain(null as Int?)
      }
    }

    "Gen.constant" should {
      "handle null value" {
        io.kotlintest.properties.forAll(Gen.constant(null as Int?)) {n ->
          n == null
        }
      }
    }

    "Gen.oneOf" should {
      "correctly handle multiple generators" {
        val gen = Gen.oneOf(Gen.positiveIntegers(), Gen.negativeIntegers())
        var positiveNumbers = 0
        var negativeNumbers = 0
        forAll(gen) {
          if (it > 0) {
            positiveNumbers++
          } else if (it < 0) {
            negativeNumbers++
          }
          it shouldNotBe 0
          true
        }
        positiveNumbers shouldBe beGreaterThan(1)
        negativeNumbers shouldBe beGreaterThan(1)
      }
    }

  }
}

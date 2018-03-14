@file:Suppress("USELESS_IS_CHECK")

package io.kotlintest.properties

import io.kotlintest.forAll
import io.kotlintest.matchers.gt
import io.kotlintest.matchers.gte
import io.kotlintest.matchers.lt
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldHave
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.matchers.substring
import io.kotlintest.specs.WordSpec
import java.util.Random

class GenTest : WordSpec() {
  init {
    "Gen.string.nextPrintableString" should {
      "give out a argument long string" {
        val random = Random()
        var rand = random.nextInt(10000)
        if (rand <= 0)
          rand = 0 - rand
        val string = Gen.string().nextPrintableString(rand)

        string.forEach {
          it.toInt() shouldBe gte(33)
          it.toInt() shouldBe lt(127)
        }
        string.length shouldBe rand
      }.config(invocations = 100, threads = 8)
    }
    "Gen.choose<int, int>" should {

      "only give out numbers in the given range" {
        val random = Random()

        val min = random.nextInt(10000) - 10000
        val max = random.nextInt(10000) + 10000

        val rand = Gen.choose(min, max).generate()
        rand shouldBe gte(min)
        rand shouldBe lt(max)
      }.config(invocations = 10000, threads = 8)

      "support negative bounds" {

        val random = Random()

        val max = random.nextInt(10000)

        val rand = Gen.choose(Int.MIN_VALUE, max).generate()
        rand shouldBe gte(Int.MIN_VALUE)
        rand shouldBe lt(max)

      }.config(invocations = 1000, threads = 8)
    }
    "Gen.choose<long, long>" should {
      "only give out numbers in the given range" {
        val random = Random()

        val min = random.nextInt(10000) - 10000
        val max = random.nextInt(10000) + 10000

        val rand = Gen.choose(min.toLong(), max.toLong()).generate()
        rand shouldBe gte(min.toLong())
        rand shouldBe lt(max.toLong())

      }.config(invocations = 10000, threads = 8)
      "support negative bounds" {
        val random = Random()

        val max = random.nextInt(10000) + 10000

        val rand = Gen.choose(Long.MIN_VALUE, max.toLong()).generate()
        rand shouldBe gte(Long.MIN_VALUE)
        rand shouldBe lt(max.toLong())

      }.config(invocations = 10000, threads = 8)
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

        forAll(table1) { clazz ->
          Gen.forClassName(clazz).generate()!!.javaClass.name shouldBe clazz
        }

        val table2 = table(
            headers("name"),
            row("kotlin.String"),
            row("kotlin.Long"),
            row("kotlin.Boolean"),
            row("kotlin.Float"),
            row("kotlin.Double")
        )

        forAll(table2) { clazz ->
          val tmp = clazz.split(".").last()
          Gen.forClassName(clazz).generate()!!.javaClass.name shouldBe "java.lang." + tmp
        }

        Gen.forClassName("kotlin.Int").generate()!!.javaClass.name shouldBe "java.lang.Integer"
      }
      "throw an exception, with a wrong class" {
        shouldThrow<IllegalArgumentException> {
          Gen.forClassName("This.is.not.a.valid.class")
        }
      }
    }
    "Gen.create" should {
      "create a Generaor with the given function" {
        Gen.create { 5 }.generate() shouldBe 5

        var i = 0
        val gen = Gen.create { i++ }
        for (n in 0..1000) {
          gen.generate() shouldBe n
        }
      }
    }
    "Gen.default" should {
      "generate the defaults for list" {

        val gen = Gen.default<List<Int>>()
        forAll(gen) {
          inst ->
          forAll(inst) {
            i ->
            (i is Int) shouldBe true
          }
          true
        }

      }


      "generate the defaults for set" {

        val gen = Gen.default<Set<String>>()
        forAll(gen) {
          inst ->
          forAll(inst) {
            i ->
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

        forAll(table) { clazz ->
          val tmp = clazz.split(".")
          Gen.forClassName(clazz).generate()!!.javaClass.name shouldHave substring(tmp[tmp.size - 1])
        }
      }
      "throw an exeption, with a wrong class" {
        shouldThrow<IllegalArgumentException> {
          Gen.forClassName("This.is.not.a.valid.class")
        }
      }
    }
    "Gen.oneOf list<T> " should {
      "choose a random member and not the same one every time" {
        val list = listOf("a", "b", "c", "d", "e")
        val gen = Gen.oneOf(list)

        val map: MutableMap<String, Int> = mutableMapOf()

        list.forEach { map.put(it, 0) }

        (0..1000000).forEach {
          val key = gen.generate()
          map.put(key, map[key]?.plus(1)!!)
        }

        map.forEach { it.value shouldBe gt(0) }

      }
      "select one of a given list" {

        forAll(Gen.list(Gen.int())) {
          list ->
          list.isNotEmpty() ||
              (0..1000).all {
                list.contains(Gen.oneOf(list).generate())
              }
        }
      }
    }
    "Gen.oneOf list<Gen<T>> " should {
      "choose one of the given Generators and generate a value from it" {
        val gen = Gen.oneOf(Gen.create { 5 },
            Gen.create { 2 },
            Gen.create { 1 })
        forAll(gen) {
          i ->
          listOf(1, 2, 5).contains(i)
        }
      }

      "always chose one of the given generators" {
        var counter = 0
        val counterGen = Gen.create { ++counter }
        val gen = Gen.oneOf(counterGen, counterGen, counterGen, counterGen, counterGen)

        var i = 0
        forAll(gen) {
          test ->
          i++
          i == test
        }

      }

    }

    "ConstGen " should {
      "always generate the same thing" {
        forAll(ConstGen(5)) {
          it == 5
        }
      }
    }

    "Gen.orNull " should {
      "have both values and nulls generated" {

        fun <T> Gen<T>.toList(size: Int): List<T> =
            ArrayList<T>(size).also { list ->
              repeat(size) {
                list += generate()
              }
            }

        val list = ConstGen(5).orNull().toList(size = 100)

        (5 in list) shouldBe true
        (null in list) shouldBe true
      }
    }

    "Gen.filter " should {
      "prevent values from being generated" {
        forAll(Gen.oneOf(listOf(1, 2, 5)).filter { it != 2 }) {
          it != 2
        }
      }
    }

    "Gen.map " should {
      "correctly transform the values" {
        forAll(ConstGen(5).map { it + 7 }) {
          it == 12
        }
      }
    }
  }
}

@file:Suppress("USELESS_IS_CHECK")

package com.sksamuel.kotlintest.properties

import io.kotlintest.*
import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.collections.contain
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.collections.shouldHaveAtMostSize
import io.kotlintest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotlintest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotlintest.matchers.floats.shouldBeGreaterThanOrEqual
import io.kotlintest.matchers.floats.shouldBeLessThanOrEqual
import io.kotlintest.matchers.gte
import io.kotlintest.matchers.lt
import io.kotlintest.matchers.numerics.shouldBeLessThanOrEqual
import io.kotlintest.matchers.string.include
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.properties.forAll
import io.kotlintest.properties.generateInfiniteSequence
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table
import java.time.*
import java.util.Random
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.collections.last
import kotlin.collections.listOf
import kotlin.collections.mutableSetOf
import kotlin.collections.plusAssign
import kotlin.collections.setOf
import kotlin.collections.toList
import kotlin.collections.toSet

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
          Gen.forClassName(clazz).random().firstOrNull()?.javaClass?.name shouldBe clazz
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
          Gen.forClassName(clazz).random().firstOrNull()?.javaClass?.name shouldBe "java.lang.$tmp"
        }

        Gen.forClassName("kotlin.Int").random().firstOrNull()?.javaClass?.name shouldBe "java.lang.Integer"
      }
      "throw an exception, with a wrong class" {
        shouldThrow<IllegalArgumentException> {
          Gen.forClassName("This.is.not.a.valid.class")
        }
      }
    }
    "Gen.create" should {
      "create a Generator with the given function" {
        Gen.create { 5 }.random().take(10).toList() shouldBe List(10) { 5 }
        var i = 0
        val gen = Gen.create { i++ }
        gen.random().take(150).toList() shouldBe List(150) { it }
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
          Gen.forClassName(clazz).random().firstOrNull()?.javaClass?.name shouldHave include(tmp[tmp.size - 1])
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
      "support covariance" {
        Gen.oneOf(
            Gen.bind(Gen.int(), X::A),
            Gen.bind(Gen.int(), X::B),
            Gen.bind(Gen.int(), X::C)
        )
      }
    }

    "Gen.numericDoubles(min, max)" should {
      val min = 1.0
      val max = 100.0

      "Should generate only numbers" {
        assertAll(10_000, Gen.numericDoubles(min, max)) {
          it.isFinite().shouldBeTrue()
        }
      }

      "Should generate everything >= min" {
        var minGenerated = false
        assertAll(10_000, Gen.numericDoubles(min, max)) {
          if(it == min) minGenerated = true
          it shouldBeGreaterThanOrEqual min
        }
        minGenerated.shouldBeTrue()
      }

      "Should generate everything <= max" {
        assertAll(10_000, Gen.numericDoubles(min, max)) {
          it shouldBeLessThanOrEqual max
        }
      }
    }

    "Gen.numericFloats(min, max)" should {
      val min = 1.0f
      val max = 100.0f

      "Should generate only numbers" {
        assertAll(10_000, Gen.numericFloats(min, max)) {
          it.isFinite().shouldBeTrue()
        }
      }

      "Should generate everything >= min" {
        assertAll(10_000, Gen.numericFloats(min, max)) {
          it shouldBeGreaterThanOrEqual min
        }
      }

      "Should generate everything <= max" {
        assertAll(10_000, Gen.numericFloats(min, max)) {
          it shouldBeLessThanOrEqual max
        }
      }
    }

    "Gen.localDate(minYear, maxYear)" should {
      "Generate valid LocalDates (no exceptions)" {
        Gen.localDate().random().take(10_000).toList()
      }

      "Generate LocalDates between minYear and maxYear" {
        val years = mutableSetOf<Int>()
        val months = mutableSetOf<Int>()
        val days = mutableSetOf<Int>()

        assertAll(10_000, Gen.localDate(1998, 1999)) {
          years += it.year
          months += it.monthValue
          days += it.dayOfMonth
        }

        years shouldBe setOf(1998, 1999)
        months shouldBe (1..12).toSet()
        days shouldBe (1..31).toSet()
      }

      "Contain Feb 29th if leap year" {
        val leapYear = 2016
        Gen.localDate(leapYear, leapYear).constants().toList() shouldContain LocalDate.of(2016, 2, 29)
      }
      
      "Contain the constants Feb 28, Jan 01 and Dec 31" {
        Gen.localDate(2019, 2020).constants().toList() shouldContainAll listOf(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31))
      }
      
      "Be the default generator for LocalDate" {
        assertAll(10) { it: LocalDate -> /* No use. Won't reach here if unsupported */ }
      }
    }

    "Gen.localTime()" should {
      "Generate valid LocalTimes(no exceptions)" {
        Gen.localTime().random().take(10_000).toList()
      }
      
      "Be the default generator for LocalTime" {
        assertAll(10) { it: LocalTime -> /* No use. Won't reach here if unsupported */ }
      }
    }

    "Gen.localDateTime(minYear, maxYear)" should {
      "Generate valid LocalDateTimes(no exceptions)" {
        Gen.localDateTime().random().take(10_000).toList()
      }

      "Generate LocalDateTimes between minYear and maxYear" {
        val years = mutableSetOf<Int>()
        val months = mutableSetOf<Int>()
        val days = mutableSetOf<Int>()
        val hours = mutableSetOf<Int>()
        val minutes = mutableSetOf<Int>()
        val seconds = mutableSetOf<Int>()

        assertAll(10_000, Gen.localDateTime(1998, 1999)) {
          years += it.year
          months += it.monthValue
          days += it.dayOfMonth
          hours += it.hour
          minutes += it.minute
          seconds += it.second
        }

        years shouldBe setOf(1998, 1999)
        months shouldBe (1..12).toSet()
        days shouldBe (1..31).toSet()
        hours shouldBe (0..23).toSet()
        minutes shouldBe (0..59).toSet()
      }
      
      "Be the default generator for LocalDateTime" {
        assertAll(10) { it: LocalDateTime -> /* No use. Won't reach here if unsupported */ }
      }
    }

    "Gen.duration(maxDuration)" should {
      "Generate only durations <= maxDuration" {
        val maxDuration = Duration.ofSeconds(120)
        
        assertAll(10_000, Gen.duration(maxDuration)) {
          it <= maxDuration
        }
      }
      
      "Generate all possible durations in the interval [0, maxDuration[" {
        val maxDuration = Duration.ofSeconds(120)
        val secondsList = mutableSetOf<Long>()
  
        assertAll(10_000, Gen.duration(maxDuration)) {
          secondsList += it.seconds
        }
        
        secondsList shouldBe (0L..119L).toSet()
      }
      
      "Be the default generator for Duration" {
        assertAll(10) { it: Duration -> /* No use. Won't reach here if unsupported */ }
      }
    }
    
    "Gen.period(maxYears)" should {
      "Generate only periods with years <= maxYears" {
        assertAll(10_000, Gen.period(2)) {
          it.years <= 2
        }
      }
      
      "Generate all possible years in the interval [0, maxYears]" {
        val generated = mutableSetOf<Int>()
        assertAll(10_000, Gen.period(10)) {
          generated += it.years
        }
        
        generated shouldBe (0..10).toSet()
      }
      
      "Generate all possible intervals for Months and Days" {
        val generatedDays = mutableSetOf<Int>()
        val generatedMonths = mutableSetOf<Int>()
        
        assertAll(10_000, Gen.period(10)) {
          generatedDays += it.days
          generatedMonths += it.months
        }
        
        generatedDays shouldBe (0..31).toSet()
        generatedMonths shouldBe (0..11).toSet()
      }
      
      "Be the default generator for Duration" {
        assertAll(10) { it: Period -> /* No use. Won't reach here if unsupported */ }
      }
      
    }
  
    "Gen.take(n)" should {
      val mockedGen = object : Gen<Int> {
        override fun constants() = listOf(1, 2)
        override fun random() = generateInfiniteSequence { 3 }
      }
      
      val mockedGen2 = object : Gen<String> {
        override fun constants() = listOf("1", "2", "3", "4")
        override fun random() = generateInfiniteSequence { "42" }
      }
      
      "Take constants first" {
        mockedGen.take(2) shouldBe listOf(1, 2)
        mockedGen2.take(4) shouldBe listOf("1", "2", "3", "4")
      }
      
      "Populate with constants + random values if constants are not enough" {
        mockedGen.take(5) shouldBe listOf(1, 2, 3, 3, 3)
        mockedGen2.take(8) shouldBe listOf("1", "2", "3", "4", "42", "42", "42", "42")
      }

      "Throw exception if the generator can't generate the amount requested" {
        val smallGen = object : Gen<String> {
          override fun constants() = listOf("1", "2", "3", "4")
          override fun random() = sequenceOf("42")
        }

        val thrown = shouldThrow<IllegalStateException> { smallGen.take(10) }
        thrown.message shouldBe "Gen could only generate 5 values while you requested 10."
      }

      "Throw exception if amount <= 0" {
        shouldThrow<IllegalArgumentException> { mockedGen.take(0) }
        shouldThrow<IllegalArgumentException> { mockedGen.take(-1) }
      }
    }

    "Gen.next(predicate)" should {

      val mockedGen = object : Gen<Int> {
        override fun constants() = listOf(1, 2)
        val seq = listOf(3, 4, 5, 6)
        override fun random() = generateInfiniteSequence { seq.random() }
      }

      "Take a random value straight from random() by default" {
        val accumulatedValues = mutableSetOf<Int>()
        repeat(1000) {
          accumulatedValues.add(mockedGen.next())
        }
        accumulatedValues shouldBe setOf(3, 4, 5, 6)
      }

      "Allows to specify a predicate to filter on the value" {
        val accumulatedValues = mutableSetOf<Int>()
        repeat(1000) {
          accumulatedValues.add(mockedGen.next { it > 4 })
        }

        accumulatedValues shouldBe setOf(5, 6)
      }
    }
    "Gen.list(maxSize)" should {
      "Generate lists of length up to 100 by default" {
        assertAll(10_000, Gen.list(Gen.double())) {
          it.shouldHaveAtMostSize(100)
        }
      }
      "Generate lists up to the given length" {
        assertAll(50, Gen.choose(1, 500)) { size: Int ->
          assertAll(1000, Gen.list(Gen.double(), size)) {
            it.shouldHaveAtMostSize(size)
          }
        }
      }
    }
    "Gen.set(maxSize)" should {
      "Generate sets of length up to 100 by default" {
        assertAll(10_000, Gen.set(Gen.double())) {
          it.shouldHaveAtMostSize(100)
        }
      }
      "Generate sets up to the given length" {
        assertAll(50, Gen.choose(1, 500)) { size: Int ->
          assertAll(1000, Gen.set(Gen.double(), size)) {
            it.shouldHaveAtMostSize(size)
          }
        }
      }
    }
    "Gen.map(keyGen, valueGen, maxSize)" should {
      val keyGen = Gen.int()
      val valueGen = Gen.double()
      "Generate maps of up to 100 elements by default" {
        assertAll(10_000, Gen.map(keyGen, valueGen)) {
          it.size.shouldBeLessThanOrEqual(100)
        }
      }
      "Generate maps of up to the given size" {
        assertAll(50, Gen.choose(1, 500)) { size: Int ->
          assertAll(1000, Gen.map(keyGen, valueGen, size)) {
            it.size.shouldBeLessThanOrEqual(size)
          }
        }
      }
    }
    "Gen.map(keyValueGen, maxSize)" should {
      val keyValueGen = Gen.int().map { Pair(it, it.toString()) }
      "Generate maps of up to 100 elements by default" {
        assertAll(10_000, Gen.map(keyValueGen)) {
          it.size.shouldBeLessThanOrEqual(100)
        }
      }
      "Generate maps of up to the given size" {
        assertAll(50, Gen.choose(1, 500)) { size: Int ->
          assertAll(1000, Gen.map(keyValueGen, size)) {
            it.size.shouldBeLessThanOrEqual(size)
          }
        }
      }
    }
  }
}

sealed class X {
  data class A(val a: Int) : X()
  data class B(val b: Int) : X()
  data class C(val c: Int) : X()
}


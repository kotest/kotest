@file:Suppress("USELESS_IS_CHECK")

package com.sksamuel.kotest.properties

import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.properties.Gen
import io.kotest.properties.assertAll
import io.kotest.properties.bind
import io.kotest.properties.bool
import io.kotest.properties.double
import io.kotest.properties.negativeIntegers
import io.kotest.properties.positiveIntegers
import io.kotest.properties.string
import io.kotest.should
import io.kotest.shouldNotBe
import io.kotest.specs.StringSpec
import io.kotest.matchers.doubles.beGreaterThan as gtd

class GenBindTest : StringSpec({

  data class FooA(val a: String)
  data class User(val email: String, val id: Int)
  data class FooC(val a: String, val b: Int, val c: Double)
  data class FooD(val a: String, val b: Int, val c: Double, val d: Int)
  data class FooE(val a: String, val b: Int, val c: Double, val d: Int, val e: Boolean)

  "Gen.bindA" {
    val gen = Gen.bind(Gen.string(), ::FooA)
    assertAll(gen) {
      it.a shouldNotBe null
    }
  }

  "Gen.bindB" {
    val gen = Gen.bind(Gen.string(), Gen.positiveIntegers(), ::User)
    assertAll(gen) {
      it.email shouldNotBe null
      it.id should beGreaterThan(0)
    }
  }

  "Gen.bindC" {
    val gen = Gen.bind(Gen.string(), Gen.positiveIntegers(), Gen.double().filter { it > 0 }, ::FooC)
    assertAll(gen) {
      it.a shouldNotBe null
      it.b should beGreaterThan(0)
      it.c should gtd(0.0)
    }
  }

  "Gen.bindD" {
    val gen = Gen.bind(Gen.string(), Gen.positiveIntegers(), Gen.double().filter { it > 0 }, Gen.negativeIntegers(), ::FooD)
    assertAll(gen) {
      it.a shouldNotBe null
      it.b should beGreaterThan(0)
      it.c should gtd(0.0)
      it.d should beLessThan(0)
    }
  }

  "Gen.bindE" {
    val gen = Gen.bind(Gen.string(), Gen.positiveIntegers(), Gen.double().filter { it > 0 }, Gen.negativeIntegers(), Gen.bool(), ::FooE)
    assertAll(gen) {
      it.a shouldNotBe null
      it.b should beGreaterThan(0)
      it.c should gtd(0.0)
      it.d should beLessThan(0)
    }
  }

})

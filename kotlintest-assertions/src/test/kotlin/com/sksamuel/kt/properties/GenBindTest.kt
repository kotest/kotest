@file:Suppress("USELESS_IS_CHECK")

package com.sksamuel.kt.properties

import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.matchers.beLessThan
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.should
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.matchers.doubles.beGreaterThan as gtd

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

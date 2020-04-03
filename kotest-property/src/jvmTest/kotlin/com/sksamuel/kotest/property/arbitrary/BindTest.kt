package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.checkAll
import io.kotest.matchers.doubles.beGreaterThan as gtd

class BindTest : StringSpec({

   data class FooA(val a: String)
   data class User(val email: String, val id: Int)
   data class FooC(val a: String, val b: Int, val c: Double)
   data class FooD(val a: String, val b: Int, val c: Double, val d: Int)
   data class FooE(val a: String, val b: Int, val c: Double, val d: Int, val e: Boolean)

   "Arb.bindA" {
      val gen = Arb.string().map { FooA(it) }
      checkAll(gen) {
         it.a shouldNotBe null
      }
   }

   "Arb.bind(a,b) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      Arb.bind(arbA, arbB) { a, b -> a + b }.take(1000).toSet().shouldHaveAtLeastSize(100)
   }

   "Arb.bindB" {
      val gen = Arb.bind(Arb.string(), Arb.positiveInts(), ::User)
      checkAll(gen) {
         it.email shouldNotBe null
         it.id should beGreaterThan(0)
      }
   }

   "Arb.bindC" {
      val gen = Arb.bind(Arb.string(), Arb.positiveInts(), Arb.double().filter { it > 0 }, ::FooC)
      checkAll(gen) {
         it.a shouldNotBe null
         it.b should beGreaterThan(0)
         it.c should gtd(0.0)
      }
   }

   "Arb.bind(a,b,c) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      val arbC = Arb.string()
      Arb.bind(arbA, arbB, arbC) { a, b, c -> "$a$b$c" }.take(1000).toSet().shouldHaveAtLeastSize(100)
   }

   "Arb.bindD" {
      val gen =
         Arb.bind(Arb.string(), Arb.positiveInts(), Arb.double().filter { it > 0 }, Arb.negativeInts(), ::FooD)
      checkAll(gen) {
         it.a shouldNotBe null
         it.b should beGreaterThan(0)
         it.c should gtd(0.0)
         it.d should beLessThan(0)
      }
   }

   "Arb.bind(a,b,c,d) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      val arbC = Arb.string()
      val arbD = Arb.string()
      Arb.bind(arbA, arbB, arbC, arbD) { a, b, c, d -> "$a$b$c$d" }.take(1000).toSet().shouldHaveAtLeastSize(100)
   }

   "Arb.bindE" {
      val gen = Arb.bind(
         Arb.string(),
         Arb.positiveInts(),
         Arb.double().filter { it > 0 },
         Arb.negativeInts(),
         Arb.bool(),
         ::FooE
      )
      checkAll(gen) {
         it.a shouldNotBe null
         it.b should beGreaterThan(0)
         it.c should gtd(0.0)
         it.d should beLessThan(0)
      }
   }

   "Arb.bind(a,b,c,d,e) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      val arbC = Arb.string()
      val arbD = Arb.string()
      val arbE = Arb.string()
      Arb.bind(arbA, arbB, arbC, arbD, arbE) { a, b, c, d, e -> "$a$b$c$d$e" }.take(1000).toSet()
         .shouldHaveAtLeastSize(100)
   }

   "Arb.reflectiveBind" {
      val arb = Arb.bind<Wobble>()
      arb.take(10).toList().size shouldBe 10
   }
})

data class Wobble(val a: String, val b: Boolean, val c: Int, val d: Double, val e: Float)

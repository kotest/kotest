package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
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
import io.kotest.property.arbitrary.withEdgecases
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


   "Arb.bind(a,b,c,d,e,f) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      val arbC = Arb.string()
      val arbD = Arb.string()
      val arbE = Arb.string()
      val arbF = Arb.string()
      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF) { a, b, c, d, e, f -> "$a$b$c$d$e$f" }.take(1000).toSet()
         .shouldHaveAtLeastSize(100)
   }

   "Arb.bind(a,b,c,d,e,f,g) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      val arbC = Arb.string()
      val arbD = Arb.string()
      val arbE = Arb.string()
      val arbF = Arb.string()
      val arbG = Arb.string()
      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG) { a, b, c, d, e, f, g -> "$a$b$c$d$e$f$g" }.take(1000).toSet()
         .shouldHaveAtLeastSize(100)
   }

   "Arb.bind(a,b,c,d,e,f,g,h) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      val arbC = Arb.string()
      val arbD = Arb.string()
      val arbE = Arb.string()
      val arbF = Arb.string()
      val arbG = Arb.string()
      val arbH = Arb.string()
      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH) { a, b, c, d, e, f, g, h -> "$a$b$c$d$e$f$g$h" }
         .take(1000).toSet().shouldHaveAtLeastSize(100)
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      val arbC = Arb.string()
      val arbD = Arb.string()
      val arbE = Arb.string()
      val arbF = Arb.string()
      val arbG = Arb.string()
      val arbH = Arb.string()
      val arbI = Arb.string()
      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, arbI) { a, b, c, d, e, f, g, h, i ->
         "$a$b$c$d$e$f$g$h$i"
      }.take(1000).toSet().shouldHaveAtLeastSize(100)
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      val arbC = Arb.string()
      val arbD = Arb.string()
      val arbE = Arb.string()
      val arbF = Arb.string()
      val arbG = Arb.string()
      val arbH = Arb.string()
      val arbI = Arb.string()
      val arbJ = Arb.string()
      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, arbI, arbJ) { a, b, c, d, e, f, g, h, i, j ->
         "$a$b$c$d$e$f$g$h$i$j"
      }.take(1000).toSet().shouldHaveAtLeastSize(100)
   }

   "Arb.bind(a,b) should compute the cartesian product of edgecases" {
      val arbA = Arb.string().withEdgecases("a")
      val arbB = Arb.string().withEdgecases("a", "b")
      Arb.bind(arbA, arbB) { a, b -> a + b }.edgecases() shouldContainExactlyInAnyOrder listOf(
         "aa",
         "ab"
      )
   }

   "Arb.bind(a,b,c) should compute the cartesian product of edgecases" {
      val arbA = Arb.string().withEdgecases("a")
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string().withEdgecases("a", "b")
      Arb.bind(arbA, arbB, arbC) { a, b, c -> a + b + c }.edgecases() shouldContainExactlyInAnyOrder listOf(
         "aaa",
         "aab",
         "aba",
         "abb",
      )
   }

   "Arb.bind(a,b,c,d) should compute the cartesian product of edgecases" {
      val arbA = Arb.string().withEdgecases("a")
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string().withEdgecases("a", "b")
      val arbD = Arb.string().withEdgecases("a", "b")
      Arb.bind(arbA, arbB, arbC, arbD) { a, b, c, d -> "$a$b$c$d" }.edgecases() shouldContainExactlyInAnyOrder listOf(
         "aaaa",
         "aaba",
         "abaa",
         "abba",
         "aaab",
         "aabb",
         "abab",
         "abbb"
      )
   }

   "Arb.bind(a,b,c,d,e) should compute the cartesian product of edgecases" {
      val arbA = Arb.string().withEdgecases("a")
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string().withEdgecases("a", "b")
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string().withEdgecases("a", "b")
      Arb.bind(arbA, arbB, arbC, arbD, arbE) { a, b, c, d, e -> "$a$b$c$d$e" }
         .edgecases() shouldContainExactlyInAnyOrder listOf(
         "aaaaa",
         "aabaa",
         "abaaa",
         "abbaa",
         "aaaba",
         "aabba",
         "ababa",
         "abbba",
         "aaaab",
         "aabab",
         "abaab",
         "abbab",
         "aaabb",
         "aabbb",
         "ababb",
         "abbbb"
      )
   }


   "Arb.bind(a,b,c,d,e,f) should compute the cartesian product of edgecases" {
      val arbA = Arb.string().withEdgecases("a", "b")
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string().withEdgecases("a", "b")
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string().withEdgecases("a", "b")
      val arbF = Arb.string().withEdgecases("a", "b")
      val expectedEdgecases =
         arbA.edgecases()
            .product(arbB.edgecases(), String::plus)
            .product(arbC.edgecases(), String::plus)
            .product(arbD.edgecases(), String::plus)
            .product(arbE.edgecases(), String::plus)
            .product(arbF.edgecases(), String::plus)

      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF) { a, b, c, d, e, f -> "$a$b$c$d$e$f" }
         .edgecases() shouldContainExactlyInAnyOrder expectedEdgecases
   }


   "Arb.bind(a,b,c,d,e,f,g) should compute the cartesian product of edgecases" {
      val arbA = Arb.string().withEdgecases("a", "b")
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string().withEdgecases("a", "b")
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string().withEdgecases("a", "b")
      val arbF = Arb.string().withEdgecases("a", "b")
      val arbG = Arb.string().withEdgecases("a", "b")
      val expectedEdgecases =
         arbA.edgecases()
            .product(arbB.edgecases(), String::plus)
            .product(arbC.edgecases(), String::plus)
            .product(arbD.edgecases(), String::plus)
            .product(arbE.edgecases(), String::plus)
            .product(arbF.edgecases(), String::plus)
            .product(arbG.edgecases(), String::plus)

      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG) { a, b, c, d, e, f, g -> "$a$b$c$d$e$f$g" }
         .edgecases() shouldContainExactlyInAnyOrder expectedEdgecases
   }


   "Arb.bind(a,b,c,d,e,f,g,h) should compute the cartesian product of edgecases" {
      val arbA = Arb.string().withEdgecases("a", "b")
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string().withEdgecases("a", "b")
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string().withEdgecases("a", "b")
      val arbF = Arb.string().withEdgecases("a", "b")
      val arbG = Arb.string().withEdgecases("a", "b")
      val arbH = Arb.string().withEdgecases("a", "b")
      val expectedEdgecases =
         arbA.edgecases()
            .product(arbB.edgecases(), String::plus)
            .product(arbC.edgecases(), String::plus)
            .product(arbD.edgecases(), String::plus)
            .product(arbE.edgecases(), String::plus)
            .product(arbF.edgecases(), String::plus)
            .product(arbG.edgecases(), String::plus)
            .product(arbH.edgecases(), String::plus)

      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH) { a, b, c, d, e, f, g, h -> "$a$b$c$d$e$f$g$h" }
         .edgecases() shouldContainExactlyInAnyOrder expectedEdgecases
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i) should compute the cartesian product of edgecases" {
      val arbA = Arb.string().withEdgecases("a", "b")
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string().withEdgecases("a", "b")
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string().withEdgecases("a", "b")
      val arbF = Arb.string().withEdgecases("a", "b")
      val arbG = Arb.string().withEdgecases("a", "b")
      val arbH = Arb.string().withEdgecases("a", "b")
      val arbI = Arb.string().withEdgecases("a", "b")
      val expectedEdgecases =
         arbA.edgecases()
            .product(arbB.edgecases(), String::plus)
            .product(arbC.edgecases(), String::plus)
            .product(arbD.edgecases(), String::plus)
            .product(arbE.edgecases(), String::plus)
            .product(arbF.edgecases(), String::plus)
            .product(arbG.edgecases(), String::plus)
            .product(arbH.edgecases(), String::plus)
            .product(arbI.edgecases(), String::plus)

      Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI
      ) { a, b, c, d, e, f, g, h, i -> "$a$b$c$d$e$f$g$h$i" }
         .edgecases() shouldContainExactlyInAnyOrder expectedEdgecases
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j) should compute the cartesian product of edgecases" {
      val arbA = Arb.string().withEdgecases("a", "b")
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string().withEdgecases("a", "b")
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string().withEdgecases("a", "b")
      val arbF = Arb.string().withEdgecases("a", "b")
      val arbG = Arb.string().withEdgecases("a", "b")
      val arbH = Arb.string().withEdgecases("a", "b")
      val arbI = Arb.string().withEdgecases("a", "b")
      val arbJ = Arb.string().withEdgecases("a", "b")
      val expectedEdgecases =
         arbA.edgecases()
            .product(arbB.edgecases(), String::plus)
            .product(arbC.edgecases(), String::plus)
            .product(arbD.edgecases(), String::plus)
            .product(arbE.edgecases(), String::plus)
            .product(arbF.edgecases(), String::plus)
            .product(arbG.edgecases(), String::plus)
            .product(arbH.edgecases(), String::plus)
            .product(arbI.edgecases(), String::plus)
            .product(arbJ.edgecases(), String::plus)

      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, arbI, arbJ) { a, b, c, d, e, f, g, h, i, j ->
         "$a$b$c$d$e$f$g$h$i$j"
      }.edgecases() shouldContainExactlyInAnyOrder expectedEdgecases
   }

   "Arb.reflectiveBind" {
      val arb = Arb.bind<Wobble>()
      arb.take(10).toList().size shouldBe 10
   }
})

data class Wobble(val a: String, val b: Boolean, val c: Int, val d: Double, val e: Float)

private fun <A, B, C> List<A>.product(listB: List<B>, fn: (A, B) -> C): List<C> =
   this.flatMap { a ->
      listB.map { b ->
         fn(a, b)
      }
   }

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
import io.kotest.property.RandomSource
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

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k) should compute the cartesian product of edgecases" {
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
      val arbK = Arb.string().withEdgecases("a", "b")
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
            .product(arbK.edgecases(), String::plus)

      Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, arbI, arbJ, arbK) { a, b, c, d, e, f, g, h, i, j, k ->
         "$a$b$c$d$e$f$g$h$i$j$k"
      }.edgecases() shouldContainExactlyInAnyOrder expectedEdgecases
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l) should compute the cartesian product of edgecases" {
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
      val arbK = Arb.string().withEdgecases("a", "b")
      val arbL = Arb.string().withEdgecases("a", "b")
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
            .product(arbK.edgecases(), String::plus)
            .product(arbL.edgecases(), String::plus)

      Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI,
         arbJ,
         arbK,
         arbL
      ) { a, b, c, d, e, f, g, h, i, j, k, l ->
         "$a$b$c$d$e$f$g$h$i$j$k$l"
      }.edgecases() shouldContainExactlyInAnyOrder expectedEdgecases
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l,m) should compute the cartesian product of edgecases" {
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
      val arbK = Arb.string().withEdgecases("a", "b")
      val arbL = Arb.string().withEdgecases("a", "b")
      val arbM = Arb.string().withEdgecases("a", "b")
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
            .product(arbK.edgecases(), String::plus)
            .product(arbL.edgecases(), String::plus)
            .product(arbM.edgecases(), String::plus)

      Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI,
         arbJ,
         arbK,
         arbL,
         arbM
      ) { a, b, c, d, e, f, g, h, i, j, k, l, m ->
         "$a$b$c$d$e$f$g$h$i$j$k$l$m"
      }.edgecases() shouldContainExactlyInAnyOrder expectedEdgecases
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l,m,n) should compute the cartesian product of edgecases" {
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
      val arbK = Arb.string().withEdgecases("a", "b")
      val arbL = Arb.string().withEdgecases("a", "b")
      val arbM = Arb.string().withEdgecases("a", "b")
      val arbN = Arb.string().withEdgecases("a", "b")
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
            .product(arbK.edgecases(), String::plus)
            .product(arbL.edgecases(), String::plus)
            .product(arbM.edgecases(), String::plus)
            .product(arbN.edgecases(), String::plus)

      Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI,
         arbJ,
         arbK,
         arbL,
         arbM,
         arbN
      ) { a, b, c, d, e, f, g, h, i, j, k, l, m, n ->
         "$a$b$c$d$e$f$g$h$i$j$k$l$m$n"
      }.edgecases() shouldContainExactlyInAnyOrder expectedEdgecases
   }

   "Arb.bind(a,b) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB) { a, b -> a + b }

      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua", "Ub"
      )
   }

   "Arb.bind(a,b,c) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arb = Arb.bind(arbA, arbB, arbC) { a, b, c -> a + b + c }

      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%", "Ubk"
      )
   }

   "Arb.bind(a,b,c,d) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC, arbD) { a, b, c, d -> a + b + c + d }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%a", "Ua%b", "Ubka", "Ubkb"
      )
   }

   "Arb.bind(a,b,c,d,e) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE) { a, b, c, d, e -> a + b + c + d + e }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%ac", "Ua%b?", "UbkaK", "UbkbC"
      )
   }

   "Arb.bind(a,b,c,d,e,f) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdgecases("x")
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF) { a, b, c, d, e, f -> a + b + c + d + e + f }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%acx", "Ua%b?x", "UbkaKx", "UbkbCx"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdgecases("x")
      val arbG = Arb.string().withEdgecases("g")
      val arb = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG
      ) { a, b, c, d, e, f, g -> a + b + c + d + e + f + g }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%acxg", "Ua%b?xg", "UbkaKxg", "UbkbCxg"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdgecases("x")
      val arbG = Arb.string().withEdgecases("g", "h")
      val arbH = Arb.string(1)
      val arb = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH
      ) { a, b, c, d, e, f, g, h -> a + b + c + d + e + f + g + h }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%acxgG", "Ua%acxhz", "Ua%b?xgP", "Ua%b?xh-", "UbkaKxgw", "UbkaKxh'", "UbkbCxgd", "UbkbCxh/"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdgecases("x")
      val arbG = Arb.string().withEdgecases("g", "h")
      val arbH = Arb.string(1)
      val arbI = Arb.string(1)
      val arb = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI
      ) { a, b, c, d, e, f, g, h, i -> a + b + c + d + e + f + g + h + i }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%acxgGs", "Ua%acxhzd", "Ua%b?xgP;", "Ua%b?xh-`", "UbkaKxgwL", "UbkaKxh'e", "UbkbCxgds", "UbkbCxh/i"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdgecases("x")
      val arbG = Arb.string().withEdgecases("g", "h")
      val arbH = Arb.string(1)
      val arbI = Arb.string(1)
      val arbJ = Arb.string().withEdgecases("j", "k")
      val arb = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI,
         arbJ
      ) { a, b, c, d, e, f, g, h, i, j -> a + b + c + d + e + f + g + h + i + j }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%acxgGsj",
         "Ua%acxgGsk",
         "Ua%acxhzdj",
         "Ua%acxhzdk",
         "Ua%b?xgP;j",
         "Ua%b?xgP;k",
         "Ua%b?xh-`j",
         "Ua%b?xh-`k",
         "UbkaKxgwLj",
         "UbkaKxgwLk",
         "UbkaKxh'ej",
         "UbkaKxh'ek",
         "UbkbCxgdsj",
         "UbkbCxgdsk",
         "UbkbCxh/ij",
         "UbkbCxh/ik"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdgecases("x")
      val arbG = Arb.string().withEdgecases("g", "h")
      val arbH = Arb.string(1)
      val arbI = Arb.string(1)
      val arbJ = Arb.string().withEdgecases("j", "k")
      val arbK = Arb.string().withEdgecases("edge")
      val arb = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI,
         arbJ,
         arbK
      ) { a, b, c, d, e, f, g, h, i, j, k -> a + b + c + d + e + f + g + h + i + j + k }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%acxgGsjedge",
         "Ua%acxgGskedge",
         "Ua%acxhzdjedge",
         "Ua%acxhzdkedge",
         "Ua%b?xgP;jedge",
         "Ua%b?xgP;kedge",
         "Ua%b?xh-`jedge",
         "Ua%b?xh-`kedge",
         "UbkaKxgwLjedge",
         "UbkaKxgwLkedge",
         "UbkaKxh'ejedge",
         "UbkaKxh'ekedge",
         "UbkbCxgdsjedge",
         "UbkbCxgdskedge",
         "UbkbCxh/ijedge",
         "UbkbCxh/ikedge"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdgecases("x")
      val arbG = Arb.string().withEdgecases("g", "h")
      val arbH = Arb.string(1)
      val arbI = Arb.string(1)
      val arbJ = Arb.string().withEdgecases("j", "k")
      val arbK = Arb.string().withEdgecases("edge")
      val arbL = Arb.string(1)
      val arb = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI,
         arbJ,
         arbK,
         arbL
      ) { a, b, c, d, e, f, g, h, i, j, k, l -> a + b + c + d + e + f + g + h + i + j + k + l }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%acxgGsjedge`",
         "Ua%acxgGskedge;",
         "Ua%acxhzdjedgej",
         "Ua%acxhzdkedgeB",
         "Ua%b?xgP;jedgeP",
         "Ua%b?xgP;kedge ",
         "Ua%b?xh-`jedge'",
         "Ua%b?xh-`kedgeB",
         "UbkaKxgwLjedgeh",
         "UbkaKxgwLkedge0",
         "UbkaKxh'ejedge*",
         "UbkaKxh'ekedge?",
         "UbkbCxgdsjedge ",
         "UbkbCxgdskedgeO",
         "UbkbCxh/ijedge7",
         "UbkbCxh/ikedge?"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l,m) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdgecases("x")
      val arbG = Arb.string().withEdgecases("g", "h")
      val arbH = Arb.string(1)
      val arbI = Arb.string(1)
      val arbJ = Arb.string().withEdgecases("j", "k")
      val arbK = Arb.string().withEdgecases("edge")
      val arbL = Arb.string(1)
      val arbM = Arb.string(1)
      val arb = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI,
         arbJ,
         arbK,
         arbL,
         arbM
      ) { a, b, c, d, e, f, g, h, i, j, k, l, m -> a + b + c + d + e + f + g + h + i + j + k + l + m }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%acxgGsjedge`0",
         "Ua%acxgGskedge;J",
         "Ua%acxhzdjedgejG",
         "Ua%acxhzdkedgeBH",
         "Ua%b?xgP;jedgePq",
         "Ua%b?xgP;kedge v",
         "Ua%b?xh-`jedge'p",
         "Ua%b?xh-`kedgeB0",
         "UbkaKxgwLjedgeh:",
         "UbkaKxgwLkedge0^",
         "UbkaKxh'ejedge*<",
         "UbkaKxh'ekedge?6",
         "UbkbCxgdsjedge  ",
         "UbkbCxgdskedgeOY",
         "UbkbCxh/ijedge7t",
         "UbkbCxh/ikedge?S"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l,m,n) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdgecases("x")
      val arbG = Arb.string().withEdgecases("g", "h")
      val arbH = Arb.string(1)
      val arbI = Arb.string(1)
      val arbJ = Arb.string().withEdgecases("j", "k")
      val arbK = Arb.string().withEdgecases("edge")
      val arbL = Arb.string(1)
      val arbM = Arb.string(1)
      val arbN = Arb.string(1).withEdgecases("_edgeN")
      val arb = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI,
         arbJ,
         arbK,
         arbL,
         arbM,
         arbN
      ) { a, b, c, d, e, f, g, h, i, j, k, l, m, n -> a + b + c + d + e + f + g + h + i + j + k + l + m + n }
      arb.edgecases(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua%acxgGsjedge`0_edgeN",
         "Ua%acxgGskedge;J_edgeN",
         "Ua%acxhzdjedgejG_edgeN",
         "Ua%acxhzdkedgeBH_edgeN",
         "Ua%b?xgP;jedgePq_edgeN",
         "Ua%b?xgP;kedge v_edgeN",
         "Ua%b?xh-`jedge'p_edgeN",
         "Ua%b?xh-`kedgeB0_edgeN",
         "UbkaKxgwLjedgeh:_edgeN",
         "UbkaKxgwLkedge0^_edgeN",
         "UbkaKxh'ejedge*<_edgeN",
         "UbkaKxh'ekedge?6_edgeN",
         "UbkbCxgdsjedge  _edgeN",
         "UbkbCxgdskedgeOY_edgeN",
         "UbkbCxh/ijedge7t_edgeN",
         "UbkbCxh/ikedge?S_edgeN"
      )
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

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
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.arbitrary.withEdges
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
      val edges = Arb.bind(arbA, arbB) { a, b -> a + b }.edges()

      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Ua",
         "%b"
      )
   }

   "Arb.bind(a,b,c) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val edges = Arb.bind(arbA, arbB, arbC) { a, b, c -> a + b + c }.edges()

      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uak", "%bc"
      )
   }

   "Arb.bind(a,b,c,d) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdges(edgecases("a", "b"))
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdges(edgecases("a", "b"))
      val edges = Arb.bind(arbA, arbB, arbC, arbD) { a, b, c, d -> a + b + c + d }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uaka", "%bca", "?aCb", "KbGb"
      )
   }

   "Arb.bind(a,b,c,d,e) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdgecases("a", "b")
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdgecases("a", "b")
      val arbE = Arb.string(1)
      val edges = Arb.bind(arbA, arbB, arbC, arbD, arbE) { a, b, c, d, e -> a + b + c + d + e }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakaz", "%bcaP", "?aCb-", "KbGbw"
      )
   }

   "Arb.bind(a,b,c,d,e,f) should compute the cartesian product of edges" {
      val arbA = Arb.string(1)
      val arbB = Arb.string().withEdges(edgecases("a", "b"))
      val arbC = Arb.string(1)
      val arbD = Arb.string().withEdges(edgecases("a", "b"))
      val arbE = Arb.string(1)
      val arbF = Arb.string().withEdges(edgecases("x"))
      val edges = Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF) { a, b, c, d, e, f -> a + b + c + d + e + f }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakazx", "%bcaPx", "?aCb-x", "KbGbwx"
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
      val edges = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG
      ) { a, b, c, d, e, f, g -> a + b + c + d + e + f + g }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakazxg", "%bcaPxg", "?aCb-xg", "KbGbwxg"
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
      val edges = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH
      ) { a, b, c, d, e, f, g, h -> a + b + c + d + e + f + g + h }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakazxg;", "%bcaPxgj", "?aCb-xgB", "KbGbwxgP", "'a/aexh ", "dbsasxh'", "da`bixhB", ";bLb`xhh"
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
      val edges = Arb.bind(
         arbA,
         arbB,
         arbC,
         arbD,
         arbE,
         arbF,
         arbG,
         arbH,
         arbI
      ) { a, b, c, d, e, f, g, h, i -> a + b + c + d + e + f + g + h + i }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakazxg;0", "%bcaPxgj*", "?aCb-xgB?", "KbGbwxgP ", "'a/aexh O", "dbsasxh'7", "da`bixhB?", ";bLb`xhh0"
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
      val edges = Arb.bind(
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
      ) { a, b, c, d, e, f, g, h, i, j -> a + b + c + d + e + f + g + h + i + j }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakazxg;0j",
         "%bcaPxgj*j",
         "?aCb-xgB?j",
         "KbGbwxgP j",
         "'a/aexh Oj",
         "dbsasxh'7j",
         "da`bixhB?j",
         ";bLb`xhh0j",
         "JaHa^xgsHk",
         "Gbqa<xgMGk",
         "va0b6xgdrk",
         "pb:b xgI0k",
         "YaSa0xh3+k",
         "tb\\aVxhI(k",
         "MajbNxhx'k",
         "kbtb-xhKUk"
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
      val edges = Arb.bind(
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
      ) { a, b, c, d, e, f, g, h, i, j, k -> a + b + c + d + e + f + g + h + i + j + k }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakazxg;0jedge",
         "%bcaPxgj*jedge",
         "?aCb-xgB?jedge",
         "KbGbwxgP jedge",
         "'a/aexh Ojedge",
         "dbsasxh'7jedge",
         "da`bixhB?jedge",
         ";bLb`xhh0jedge",
         "JaHa^xgsHkedge",
         "Gbqa<xgMGkedge",
         "va0b6xgdrkedge",
         "pb:b xgI0kedge",
         "YaSa0xh3+kedge",
         "tb\\aVxhI(kedge",
         "MajbNxhx'kedge",
         "kbtb-xhKUkedge"
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
      val edges = Arb.bind(
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
      ) { a, b, c, d, e, f, g, h, i, j, k, l -> a + b + c + d + e + f + g + h + i + j + k + l }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakazxg;0jedge(",
         "%bcaPxgj*jedgej",
         "?aCb-xgB?jedge_",
         "KbGbwxgP jedge#",
         "'a/aexh Ojedge9",
         "dbsasxh'7jedgeZ",
         "da`bixhB?jedgee",
         ";bLb`xhh0jedgey",
         "JaHa^xgsHkedgeJ",
         "Gbqa<xgMGkedgeT",
         "va0b6xgdrkedgea",
         "pb:b xgI0kedge)",
         "YaSa0xh3+kedgeN",
         "tb\\aVxhI(kedge=",
         "MajbNxhx'kedge&",
         "kbtb-xhKUkedge\$"
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
      val edges = Arb.bind(
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
      ) { a, b, c, d, e, f, g, h, i, j, k, l, m -> a + b + c + d + e + f + g + h + i + j + k + l + m }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakazxg;0jedge(b",
         "%bcaPxgj*jedgej_",
         "?aCb-xgB?jedge_s",
         "KbGbwxgP jedge#T",
         "'a/aexh Ojedge9w",
         "dbsasxh'7jedgeZH",
         "da`bixhB?jedgee-",
         ";bLb`xhh0jedgey&",
         "JaHa^xgsHkedgeJ?",
         "Gbqa<xgMGkedgeTl",
         "va0b6xgdrkedgeaf",
         "pb:b xgI0kedge)o",
         "YaSa0xh3+kedgeNT",
         "tb\\aVxhI(kedge=h",
         "MajbNxhx'kedge&l",
         "kbtb-xhKUkedge\$L"
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
      val edges = Arb.bind(
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
      ) { a, b, c, d, e, f, g, h, i, j, k, l, m, n -> a + b + c + d + e + f + g + h + i + j + k + l + m + n }.edges()
      edges.values(RandomSource.seeded(1234L)) shouldContainExactlyInAnyOrder listOf(
         "Uakazxg;0jedge(b_edgeN",
         "%bcaPxgj*jedgej__edgeN",
         "?aCb-xgB?jedge_s_edgeN",
         "KbGbwxgP jedge#T_edgeN",
         "'a/aexh Ojedge9w_edgeN",
         "dbsasxh'7jedgeZH_edgeN",
         "da`bixhB?jedgee-_edgeN",
         ";bLb`xhh0jedgey&_edgeN",
         "JaHa^xgsHkedgeJ?_edgeN",
         "Gbqa<xgMGkedgeTl_edgeN",
         "va0b6xgdrkedgeaf_edgeN",
         "pb:b xgI0kedge)o_edgeN",
         "YaSa0xh3+kedgeNT_edgeN",
         "tb\\aVxhI(kedge=h_edgeN",
         "MajbNxhx'kedge&l_edgeN",
         "kbtb-xhKUkedge\$L_edgeN"
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

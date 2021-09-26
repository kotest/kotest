package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.positiveInt
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
      val gen = Arb.bind(Arb.string(), Arb.positiveInt(), ::User)
      checkAll(gen) {
         it.email shouldNotBe null
         it.id should beGreaterThan(0)
      }
   }

   "Arb.bindC" {
      val gen = Arb.bind(Arb.string(), Arb.positiveInt(), Arb.double().filter { it > 0 }, ::FooC)
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
         Arb.bind(Arb.string(), Arb.positiveInt(), Arb.double().filter { it > 0 }, Arb.negativeInt(), ::FooD)
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
         Arb.positiveInt(),
         Arb.double().filter { it > 0 },
         Arb.negativeInt(),
         Arb.boolean(),
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

   "Arb.bind(a,b) should compute the probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB) { a, b -> a + b }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "ab",
         "ab",
         "aa",
         "ab",
         "aa"
      )
   }

   "Arb.bind(a,b,c) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases(emptyList())
      val arb = Arb.bind(arbA, arbB, arbC) { a, b, c -> a + b + c }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "abk",
         "abK",
         "abz",
         "aaw",
         "ab/"
      )
   }

   "Arb.bind(a,b,c,d) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases(emptyList())
      val arb = Arb.bind(arbA, arbB, arbC, arbD) { a, b, c, d -> "$a$b$c$d" }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "abb ",
         "aaaC",
         "aabV",
         "abad",
         "aabb"
      )
   }

   "Arb.bind(a,b,c,d,e) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases(emptyList())
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE) { a, b, c, d, e -> "$a$b$c$d$e" }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "abkbb",
         "aaCab",
         "aa-ba",
         "ab/aa",
         "aa`aa"
      )
   }


   "Arb.bind(a,b,c,d,e,f) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases(emptyList())
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases(emptyList())
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF) { a, b, c, d, e, f -> "$a$b$c$d$e$f" }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "bbkbb?",
         "aaGaaP",
         "ba'ab/",
         "bb;bbL",
         "bbiab;"
      )
   }


   "Arb.bind(a,b,c,d,e,f,g) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases(emptyList())
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases(emptyList())
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG) { a, b, c, d, e, f, g -> "$a$b$c$d$e$f$g" }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "%babb?a",
         "#baabVa",
         "'aaabsb",
         ":baaa\\b",
         "`babaBa"
      )
   }


   "Arb.bind(a,b,c,d,e,f,g,h) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases(emptyList())
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases(emptyList())
      val arb =
         Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH) { a, b, c, d, e, f, g, h -> "$a$b$c$d$e$f$g$h" }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "babacbbK",
         "aaaaPbaw",
         "aaabsbb;",
         "aaabsbb`",
         "babbPbb'"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases(emptyList())
      val arbC = Arb.string(1).withEdgecases("a")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases(emptyList())
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases("a", "b")
      val arbI = Arb.string(1).withEdgecases("a", "b")
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
      ) { a, b, c, d, e, f, g, h, i -> "$a$b$c$d$e$f$g$h$i" }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "boabb?aaa",
         "bzaab8aba",
         "azabb;bba",
         "bsabaAaba",
         "aEaabBaaa"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases(emptyList())
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases(emptyList())
      val arbI = Arb.string(1).withEdgecases("a", "b")
      val arbJ = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, arbI, arbJ) { a, b, c, d, e, f, g, h, i, j ->
         "$a$b$c$d$e$f$g$h$i$j"
      }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "boababbKaa",
         "aVababa'ab",
         "a2bbabbLab",
         "b\\bbabaBab",
         "aabaaaa*ab"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases(emptyList())
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases("a", "b")
      val arbI = Arb.string(1).withEdgecases("a", "b")
      val arbJ = Arb.string(1).withEdgecases(emptyList())
      val arbK = Arb.string(1).withEdgecases("a", "b")
      val arb =
         Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, arbI, arbJ, arbK) { a, b, c, d, e, f, g, h, i, j, k ->
            "$a$b$c$d$e$f$g$h$i$j$k"
         }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "babacbbaaCa",
         "aaab8abaaSb",
         "baba`aaabsb",
         "baabYbabb=b",
         "aaaa*abbaOa"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases(emptyList())
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases("a", "b")
      val arbI = Arb.string(1).withEdgecases("a", "b")
      val arbJ = Arb.string(1).withEdgecases("a")
      val arbK = Arb.string(1).withEdgecases("a", "b")
      val arbL = Arb.string(1).withEdgecases(emptyList())
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
      ) { a, b, c, d, e, f, g, h, i, j, k, l ->
         "$a$b$c$d$e$f$g$h$i$j$k$l"
      }

      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "bbkbbbbaaaaG",
         "ba-babaababs",
         "ba`aaabbbab`",
         "baBabbbababh",
         "ab?babbabaa0"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l,m) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases(emptyList())
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases(emptyList())
      val arbH = Arb.string(1).withEdgecases("a", "b")
      val arbI = Arb.string(1).withEdgecases("a", "b")
      val arbJ = Arb.string(1).withEdgecases("a")
      val arbK = Arb.string(1).withEdgecases("a", "b")
      val arbL = Arb.string(1).withEdgecases("a", "b")
      val arbM = Arb.string(1).withEdgecases(emptyList())
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
      ) { a, b, c, d, e, f, g, h, i, j, k, l, m ->
         "$a$b$c$d$e$f$g$h$i$j$k$l$m"
      }

      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "%baabbWaaaabz",
         "-baaaaSbaabb;",
         "Lababb\\bbabaB",
         " ababa#aaaab ",
         "7aaaaapbaaaav"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l,m,n) should compute probabilistic edge cases" {
      val arbA = Arb.string(1).withEdgecases("a")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases(emptyList())
      val arbI = Arb.string(1).withEdgecases("a", "b")
      val arbJ = Arb.string(1).withEdgecases("a", "b")
      val arbK = Arb.string(1).withEdgecases("a", "b")
      val arbL = Arb.string(1).withEdgecases("a", "b")
      val arbM = Arb.string(1).withEdgecases("a", "b")
      val arbN = Arb.string(1).withEdgecases(emptyList())
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
      ) { a, b, c, d, e, f, g, h, i, j, k, l, m, n ->
         "$a$b$c$d$e$f$g$h$i$j$k$l$m$n"
      }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(
         "abbabbbWaaaabz",
         "aababaaSbaabb;",
         "aaabbbb\\bbabaB",
         "abababa#aabab ",
         "abaabaapbabaav"
      )
   }

   "Arb.bind list" {
      val arbs: List<Arb<String>> = generateSequence { Arb.string(1).withEdgecases("a") }.take(100).toList()
      val arb: Arb<String> = Arb.bind(arbs) { it.joinToString("") }

      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()

      edgeCases shouldContainExactly listOf(
         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
      )
   }

   "Arb.bind shrinks" {
      data class Person(val name: String, val age: Int)

      val arb = Arb.bind<Person>()

      val stdout = captureStandardOut {
         shouldThrowAny {
            checkAll(arb) { person ->
               person.name.length shouldBeLessThan 10
               person.age shouldBeGreaterThan -1
               person.age shouldBeLessThan 130
            }
         }
      }

      stdout shouldContain "Shrink result"
      stdout shouldContain "Person(name=, age=-1)"
   }
})

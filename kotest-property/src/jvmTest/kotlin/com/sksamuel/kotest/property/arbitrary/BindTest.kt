package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
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

   "Arb.bind(a,b) should compute the probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB) { a, b -> a + b }
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "aa",
         "ab",
         "ab",
         "ab",
         "aa"
      )
   }

   "Arb.bind(a,b,c) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC) { a, b, c -> a + b + c }
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "xbb",
         "aba",
         "aaa",
         "aba",
         "aba"
      )
   }

   "Arb.bind(a,b,c,d) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC, arbD) { a, b, c, d -> "$a$b$c$d" }
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "abba",
         "ajab",
         "abaa",
         "aaba",
         "abaa"
      )
   }

   "Arb.bind(a,b,c,d,e) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE) { a, b, c, d, e -> "$a$b$c$d$e" }
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "aabaa",
         "jaba0",
         "aabab",
         "abbab",
         "abbbb"
      )
   }


   "Arb.bind(a,b,c,d,e,f) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF) { a, b, c, d, e, f -> "$a$b$c$d$e$f" }
      val rs = RandomSource.seeded(1234L)
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "aaebab",
         "aba0aa",
         "]abaab",
         "aababb",
         "bbbaba"
      )
   }


   "Arb.bind(a,b,c,d,e,f,g) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG) { a, b, c, d, e, f, g -> "$a$b$c$d$e$f$g" }
      val rs = RandomSource.seeded(1234L)
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "babbabb",
         "ba0aaba",
         "aabbbab",
         "baaabab",
         "babaabb"
      )
   }


   "Arb.bind(a,b,c,d,e,f,g,h) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases("a", "b")
      val arb =
         Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH) { a, b, c, d, e, f, g, h -> "$a$b$c$d$e$f$g$h" }
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "baabbaBb",
         "a0aabaab",
         "aaaabaab",
         "aab0abba",
         "bab\$aTba"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
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
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "ebabbabbb",
         "0aabaabaa",
         "abaaEbbba",
         "babaabaab",
         "b\$aTbabab"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases(emptyList())
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases("a", "b")
      val arbI = Arb.string(1).withEdgecases("a", "b")
      val arbJ = Arb.string(1).withEdgecases("a", "b")
      val arb = Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, arbI, arbJ) { a, b, c, d, e, f, g, h, i, j ->
         "$a$b$c$d$e$f$g$h$i$j"
      }
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "bSabbabaja",
         "b baabaaba",
         "b aabbbaba",
         "b(abbbbaba",
         "a)abababba"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
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
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "abbaebbYbBa",
         "bbaa6ababFb",
         "Ebbbdbabbra",
         "abbboaba5eT",
         "ababLbbba-W"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases("a", "b")
      val arbI = Arb.string(1).withEdgecases("a", "b")
      val arbJ = Arb.string(1).withEdgecases("a", "b")
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

      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "aabbabajabau",
         "bbaaabbbaaaS",
         "Ebbbabaaabab",
         "abbbabaabbas",
         "aaaabaabaa>b"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l,m) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases("a", "b")
      val arbI = Arb.string(1).withEdgecases("a", "b")
      val arbJ = Arb.string(1).withEdgecases("a", "b")
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

      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "abbabbbaaba0?",
         "aaabaababaabM",
         "abbbabaaabab'",
         "bbbababa5abaH",
         "ababaMaa>aWbp"
      )
   }

   "Arb.bind(a,b,c,d,e,f,g,h,i,j,k,l,m,n) should compute probabilistic edgecases" {
      val arbA = Arb.string(1).withEdgecases("a", "b")
      val arbB = Arb.string(1).withEdgecases("a", "b")
      val arbC = Arb.string(1).withEdgecases("a", "b")
      val arbD = Arb.string(1).withEdgecases("a", "b")
      val arbE = Arb.string(1).withEdgecases("a", "b")
      val arbF = Arb.string(1).withEdgecases("a", "b")
      val arbG = Arb.string(1).withEdgecases("a", "b")
      val arbH = Arb.string(1).withEdgecases("a", "b")
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
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgecases shouldContainExactly listOf(
         "baBbajaba0aab=",
         "baababaabaaEb-",
         "abaaababaabaah",
         "abab\$aTbababbl",
         "bbbaJa\\baaabaQ"
      )
   }

   "Arb.bind list" {
      val arbs: List<Arb<String>> = generateSequence { Arb.string(1).withEdgecases("a") }.take(100).toList()
      val arb: Arb<String> = Arb.bind(arbs) { it.joinToString("") }

      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()

      edgecases shouldContainExactly listOf(
         "aaaaaaaaaa>aWaaaaaaaaa^aaaaa!aa#aaaaaaaaaa@aaaaaa5aaaXaa7aaaPaaaaaaOa aaraa5aaaaaaaaaaaaaaaaaafa<aaa",
         "aAaaaaaa;aaaa'_aaaaaaaaaaaaaW1e\\d_B]\\u)3ivaaaaaaaa1aaaaaaaaaaaaaaaaaaaaaaaaaaa>maaaa_aaa!aaaaaaaa@aa",
         "aazaaaaaaaaaaaaaaaaaaEaaaaaaaaaawaaaaaaaa)aaaa`aaaaavaaaaaaaaYAaaa\\.aaaaaaaaaaaaaaaaaMaaaaaaaaaaaaaa",
         "aaaaaaaaaeaaaaaaaaaaaaaaaNaaaaaaaaaaaabaaaaBaa5aaaaaaaaWaaaaaaQa=aa0aaaaaaaaaaaaabaaa?aaaaaaaaaaaaaa",
         "aaaaaahaaaaaaaaaaaaaaaaaa?asaaaaamaaaaaaaaaaaaaaaaaaaaaaaa^a_aaaaaaaaaLT(LhOVxH@36^Aaaaaaaa`aaataaaa"
      )
   }

   "Arb.reflectiveBind" {
      val arb = Arb.bind<Wobble>()
      arb.take(10).toList().size shouldBe 10
   }

   "Arb.reflectiveBind should generate probabilistic edgecases" {
      val arb = Arb.bind<Wobble>()
      val edgecases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()

      edgecases shouldContainExactly listOf(
         Wobble(a = "", b = false, c = -1, d = 1.7976931348623157E308, e = 1.4E-45f),
         Wobble(a = "", b = true, c = -2147483648, d = 1.0, e = -1.0f),
         Wobble(a = "", b = true, c = 1, d = 1.0E300, e = 3.4028235E38f),
         Wobble(a = "a", b = false, c = -2147483648, d = Double.POSITIVE_INFINITY, e = 1.4E-45f),
         Wobble(a = "a", b = false, c = 1, d = -1.0E300, e = 0.0f)
      )
   }
})

data class Wobble(val a: String, val b: Boolean, val c: Int, val d: Double, val e: Float)

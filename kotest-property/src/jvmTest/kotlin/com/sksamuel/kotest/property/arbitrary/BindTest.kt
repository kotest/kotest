package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.arbitrary.zip
import io.kotest.property.checkAll
import io.kotest.matchers.doubles.beGreaterThan as gtd

@EnabledIf(LinuxCondition::class)
class BindTest : StringSpec({

   data class User(val email: String, val id: Int)
   data class FooC(val a: String, val b: Int, val c: Double)
   data class FooD(val a: String, val b: Int, val c: Double, val d: Int)
   data class FooE(val a: String, val b: Int, val c: Double, val d: Int, val e: Boolean)

   "Arb.bind(a,b) should generate distinct values" {
      val arbA = Arb.string()
      val arbB = Arb.string()
      Arb.bind(arbA, arbB) { a, b -> a + b }.take(1000).toSet().shouldHaveAtLeastSize(100)
      Arb.zip(arbA, arbB) { a, b -> a + b }.take(1000).toSet().shouldHaveAtLeastSize(100)
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

   "Arb.zipC" {
      val gen = Arb.zip(Arb.string(), Arb.positiveInt(), Arb.double().filter { it > 0 }, ::FooC)
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

   "Arb.zipE" {
      val gen = Arb.zip(
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
         "ab<",
         "abZ",
         "ab<",
         "aad",
         "ab."
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
         "abbM",
         "aaa\$",
         "aabh",
         "abau",
         "aab\""
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
         "ab<bb",
         "aa\$ab",
         "aa&ba",
         "ab.aa",
         "aanaa"
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
         "bb<bbG",
         "aa;aaq",
         "baDab.",
         "bb{bbX",
         "bb.ab6"
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
         "[babbGa",
         "xbaabha",
         "DaaabQb",
         "1baaaXb",
         "Lbaba+a"
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
         "baba:bbZ",
         "aaaaqbad",
         "aaabQbb{",
         "aaabFbbL",
         "babbFbbO"
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
         "bsabbGaaa",
         "b<aabCaba",
         "a\$abb{bba",
         "bFabaJaba",
         "agaabeaaa"
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
         "bsababbZaa",
         "auababaDab",
         "aDbbabbXab",
         "b1bbaba+ab",
         "aZbaaaaxab"
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
         "baba:bbaa\$a",
         "aaabCabaaub",
         "babanaaabFb",
         "baabKbabb4b",
         "aaaaxabbafa"
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
         "bb<bbbbaaaa;",
         "ba&babaababQ",
         "banaaabbbabL",
         "ba+abbbabab0",
         "abCbabbabaa@"
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
         "[baabbqaaaab<",
         "&baaaaubaabb{",
         "Xababb1bbaba+",
         "*ababaqaaaab]",
         "iaaaaa\$baaaaM"
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
         "abbabbbqaaaab<",
         "aababaaubaabb{",
         "aaabbbb1bbaba+",
         "abababaqaabab]",
         "abaabaa\$babaaM"
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

   "!Arb.bind shrinks" {
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

   "Bind using properties" {
      checkAll(Arb.bind<User> {
         bind(User::email to Arb.string().map { s -> "$s@yahoo.com" })
      }) { user ->
         user.email shouldEndWith "@yahoo.com"
      }
   }

   "Binding properties in a nested structure should work" {
      data class Person(val id: Int, val name: String)
      data class Family(val name: String, val persons: List<Person>)

      checkAll(Arb.bind<Family> {
         bind(Family::name to Arb.string().map { s -> "Flanders-$s" })
         bind(Person::id to Arb.positiveInt())
      }) { family ->
         family.name shouldStartWith "Flanders-"
         family.persons.forAll {
            it.name shouldNotStartWith "Flanders-"
            it.id shouldBeGreaterThan 0
         }
      }
   }

   "When binding using properties and classes, properties should take precedence no matter the order of binding" {
      checkAll(Arb.bind<User> {
         bind(Int::class to Arb.constant(3))
         bind(User::id to Arb.constant(7))
      }) {
         it.id shouldBe 7
      }

      checkAll(Arb.bind<User> {
         bind(User::id to Arb.constant(7))
         bind(Int::class to Arb.constant(3))
      }) {
         it.id shouldBe 7
      }
   }

   "When binding nullable properties to an arb that does not generate nulls, then no nulls should be implicitly added" {
      data class Bar(val baz: String)
      data class Foo(val bar: Bar?)

      val arbBarNotNull = Arb.bind<Bar>()
      val arbFooWithBar: Arb<Foo> = Arb.bind<Foo> {
         bind(Foo::bar to arbBarNotNull) // field should never be null since its Arb does not generate nulls
      }

      checkAll(arbFooWithBar) { foo ->
         foo.bar != null
      }
   }

})

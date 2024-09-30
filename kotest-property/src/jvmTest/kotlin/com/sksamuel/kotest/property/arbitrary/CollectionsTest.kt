package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.retry
import io.kotest.assertions.retryConfig
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.exist
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.Exhaustive
import io.kotest.property.PropTestConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.constant
import io.kotest.property.exhaustive.of
import io.kotest.property.forAll
import kotlin.time.Duration.Companion.seconds

class CollectionsTest : DescribeSpec({

   describe("Arb.element should") {
      it("not construct an arb from an empty list") {
         shouldThrow<IllegalArgumentException> { Arb.of(emptyList<String>()) }
      }

      it("not construct an arb from an empty vararg") {
         shouldThrow<IllegalArgumentException> { Arb.of<String>() }
      }

      it("yield a random sample from the provided collection") {
         val expected = listOf("bar", "foo", "baz", "foo", "baz")
         Arb.of(listOf("foo", "bar", "baz")).take(5, RandomSource.seeded(1234L)).toList() shouldBe expected
      }

      it("yield a random sample from the provided vararg") {
         val expected = listOf("bar", "foo", "baz", "foo", "baz")
         Arb.of("foo", "bar", "baz").take(5, RandomSource.seeded(1234L)).toList() shouldBe expected
      }
   }

   describe("Arb.list should") {

      it("not include empty edge cases as first sample") {
         val numGen = Arb.list(Arb.int(), 1..10)
         forAll(1, numGen) { it.isNotEmpty() }
      }

      it("return lists of underlying generators") {
         val gen = Arb.list(Exhaustive.constant(1), 2..10)
         checkAll(gen) {
            it.shouldHaveAtLeastSize(2)
            it.shouldHaveAtMostSize(10)
            it.toSet() shouldBe setOf(1)
         }
      }

      it("include repeated elements in edge cases") {
         val edgeCase = Arb.positiveInt().edgecases().firstOrNull()
         Arb.list(Arb.positiveInt()).edgecases() shouldContain listOf(edgeCase, edgeCase)
         Arb.list(Arb.positiveInt(), 4..6).edgecases() shouldContain listOf(edgeCase, edgeCase, edgeCase, edgeCase)

      }

      it("include empty list in edge cases") {
         Arb.list(Arb.positiveInt()).edgecases() shouldContain emptyList()
      }

      it("respect bounds in edge cases") {
         val edges = Arb.list(Arb.positiveInt(), 2..10).edgecases().toSet()
         edges.forAll { it.shouldNotBeEmpty() }
      }

      it("generate lists of length up to 100 by default") {
         checkAll(10_000, Arb.list(Arb.double())) {
            it.shouldHaveAtMostSize(100)
         }
      }

      it("generate lists in the given range") {
         checkAll(1000, Arb.list(Arb.double(), 250..500)) {
            it.shouldHaveAtLeastSize(250)
            it.shouldHaveAtMostSize(500)
         }
      }

      it("support underlying generators returning null") {
         checkAll(
            Exhaustive.of(
               Exhaustive.constant(null),
               Arb.constant(null),
               Arb.constant("").orNull(1.0),
               Arb.constant(0).orNull(1.0)
            ),
            Exhaustive.of(0..100, 1..10, 2..50),
         ) { gen, range ->
            shouldNotThrowAny {
               Arb.list(gen, range).checkAll(PropTestConfig(edgeConfig = EdgeConfig(0.5))) {
                  it.shouldHaveAtLeastSize(range.first)
                  it.shouldHaveAtMostSize(range.last)
                  it shouldNot exist { value -> value != null }
               }
            }
         }
      }

      it("not fail on edge cases for non-empty lists with null values") {
         shouldNotThrowAny {
            Arb.list(Arb.constant(null), 1..100).edgecases().forAll {}
            Arb.list(Exhaustive.constant(null), 1..100).edgecases().forAll {}
         }
      }

      it("maintain performance fixed by https://github.com/kotest/kotest/issues/4016").config(timeout = 2.seconds) {
         /*
         if we revert the fix as follows, the test fails:
         git revert 8ba8975 --no-commit
          */
         val innerGen0 = arbitrary {
            Box(
               Arb.string().bind(),
               Arb.list(Arb.string(), 0..5).bind()
            )
         }
         val outerGen = arbitrary {
            OuterBox(
               Arb.string().bind(),
               Arb.list(innerGen0, 0..5).bind()
            )
         }
         checkAll(1000, outerGen) { box ->
            box.shouldNotBeNull()
         }
      }
   }

   describe("Arb.set should") {

      it("not include empty edge cases as first sample") {
         val numGen = Arb.set(Arb.int(), 1..10)
         forAll(1, numGen) { it.isNotEmpty() }
      }

      it("throw when underlying arb cardinality is lower than expected set cardinality") {
         val arbUnderlying = Arb.of("foo", "bar", "baz")
         shouldThrowAny {
            Arb.set(arbUnderlying, 5..100).single()
         }
      }

      it("generate when sufficient cardinality is available, even if dups are periodically generated") {
         // This test will fail with a small probability, adding retries to eliminate flakiness
         retry(retryConfig { maxRetry = 20 }) {
            // this arb will generate 100 ints, but the first 1000 we take are almost certain to not be unique,
            // so this test will ensure, as long as the arb can still complete, it does.
            val arbUnderlying = Arb.int(0..1000)
            Arb.set(arbUnderlying, 1000).single()
         }
      }

      it("generate when sufficient cardinality is available, regardless of size") {
         val arbUnderlying = Arb.int()
         Arb.set(arbUnderlying, 1000000).single()
      }

      it("generate sets of length up to 100 by default") {
         checkAll(10_000, Arb.set(Arb.double())) {
            it.shouldHaveAtMostSize(100)
         }
      }

      it("generate sets in the given range") {
         checkAll(1000, Arb.set(Arb.double(), 250..500)) {
            it.shouldHaveAtLeastSize(250)
            it.shouldHaveAtMostSize(500)
         }
      }
   }
}) {
   private data class Box(
      val name: String,
      val contents: List<String>
   )

   private data class OuterBox(
      val name: String,
      val contents: List<Box>
   )
}

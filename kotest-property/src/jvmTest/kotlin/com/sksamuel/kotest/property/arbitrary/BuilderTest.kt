package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.comparables.shouldBeBetween
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLengthBetween
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.IntShrinker
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.generateArbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.numbers.IntClassifier
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.asSample
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.random.nextInt

@EnabledIf(LinuxCondition::class)
class BuilderTest : FunSpec() {
   init {

      test("custom arb test") {
         arbitrary {
            it.random.nextInt(3..6)
         }.take(1000).toSet() shouldBe setOf(3, 4, 5, 6)
      }

      test("composition of arbs") {
         data class Person(val name: String, val age: Int)

         val personArb = arbitrary { rs ->
            val name = Arb.string(10..12).next(rs)
            val age = Arb.int(21, 150).next(rs)
            Person(name, age)
         }

         personArb.next().name.shouldHaveLengthBetween(10, 12)
         personArb.next().age.shouldBeBetween(21, 150)
      }

      context("arbitrary builder using restricted continuation") {
         test("should be stack safe") {
            val arb: Arb<Int> = arbitrary {
               (1..100000).map {
                  Arb.int().bind()
               }.last()
            }

            val result = shouldNotThrowAny { arb.single(RandomSource.seeded(1234)) }
            result shouldBe -1486934023
         }

         test("should be equivalent to chaining flatMaps") {
            val arbFlatMaps: Arb<String> =
               Arb.string(5, Codepoint.alphanumeric()).withEdgecases("edge1", "edge2").flatMap { first ->
                  Arb.int(1..9).withEdgecases(5).flatMap { second ->
                     Arb.int(101..109).withEdgecases(100 + second).map { third ->
                        "$first $second $third"
                     }
                  }
               }

            val arb: Arb<String> = arbitrary {
               val first = Arb.string(5, Codepoint.alphanumeric()).withEdgecases("edge1", "edge2").bind()
               val second = Arb.int(1..9).withEdgecases(5).bind()
               val third = Arb.int(101..109).withEdgecases(100 + second).bind()
               "$first $second $third"
            }

            val flatMapsResult = arbFlatMaps.generate(RandomSource.seeded(12345L)).take(100).map { it.value }.toList()
            val builderResult = arb.generate(RandomSource.seeded(12345L)).take(100).map { it.value }.toList()

            // should be equivalent
            builderResult shouldContainExactly flatMapsResult
         }

         test("should bind edgecases") {
            val arb: Arb<String> = arbitrary {
               val first = Arb.string(5, Codepoint.alphanumeric()).withEdgecases("edge1", "edge2").bind()
               val second = Arb.int(1..9).withEdgecases(5).bind()
               val third = Arb.int(101..109).withEdgecases(100 + second, 109).bind()
               "$first $second $third"
            }

            arb.edgecases() shouldContainExactlyInAnyOrder setOf(
               "edge1 5 105",
               "edge2 5 105",
               "edge1 5 109",
               "edge2 5 109",
            )
         }

         test("should preserve edgecases of dependent arbs, even when intermideary arb(s) have no edgecases") {

            val arb: Arb<String> = arbitrary {
               val first = Arb.string(5, Codepoint.alphanumeric()).withEdgecases("edge1", "edge2").bind()
               val second = Arb.int(1..4).withEdgecases(emptyList()).bind()
               val third = Arb.int(101..109).withEdgecases(100 + second).bind()
               "$first $second $third"
            }

            arb.edgecases() shouldContainExactlyInAnyOrder setOf(
               "edge1 1 101",
               "edge1 2 102",
               "edge1 3 103",
               "edge1 4 104",
               "edge2 1 101",
               "edge2 2 102",
               "edge2 3 103",
               "edge2 4 104"
            )
         }

         test("should assign edgecases") {
            val edges = setOf("edge1", "edge2")
            val arb = arbitrary(edges.toList()) { "abcd" }

            arb.edgecases() shouldContainExactlyInAnyOrder edges
         }

         test("should assign edgecases and shrinker") {
            val shrinker = IntShrinker(1..5)
            val edges = setOf(1, 2)
            val arb = arbitrary(edges.toList(), shrinker) { 5 }

            arb.edgecases() shouldContainExactlyInAnyOrder edges
            arb.sample(RandomSource.seeded(1234L)).shrinks.children.value.map { it.value() } shouldBe shrinker.shrink(5)
         }

         test("should use shrinker when provided") {
            val shrinker = IntShrinker(1..5)
            val arb = arbitrary(shrinker) { 5 }

            arb.classifier.shouldBeNull()

            val shrinks = arb.sample(RandomSource.seeded(1234L)).shrinks
            shrinks.children.value.map { it.value() } shouldContainExactly shrinker.shrink(5)
         }

         test("should use classifier when provided") {
            val classifier = IntClassifier(1..5)
            val arb = arbitrary(classifier) { 5 }
            arb.classifier shouldBeSameInstanceAs classifier
         }

         test("should use classifier and shrinker when provided") {
            val shrinker = IntShrinker(1..5)
            val classifier = IntClassifier(1..5)
            val arb = arbitrary(shrinker, classifier) { 5 }

            arb.classifier shouldBeSameInstanceAs classifier

            val shrinks = arb.sample(RandomSource.seeded(1234L)).shrinks
            shrinks.children.value.map { it.value() } shouldContainExactly shrinker.shrink(5)
         }

         test("should use edgecase function when provided") {
            val arb = arbitrary({ 5.asSample() }) { 10 }
            arb.edgecases() shouldContainExactlyInAnyOrder setOf(5)
         }

         test("should use edgecase function and shrinker when provided") {
            val shrinker = IntShrinker(1..5)
            val arb = arbitrary({ 5.asSample() }, shrinker) { 10 }

            arb.edgecases() shouldContainExactlyInAnyOrder setOf(5)

            val shrinks = arb.sample(RandomSource.seeded(1234L)).shrinks
            shrinks.children.value.map { it.value() } shouldContainExactly shrinker.shrink(10)
         }

         test("should support .bind() syntax") {
            val arb = Arb.constant(5)
            val shrinker = IntShrinker(1..5)
            val classifier = IntClassifier(1..5)
            arbitrary { arb.bind() }.single() shouldBe 5
            arbitrary(shrinker) { arb.bind() }.single() shouldBe 5
            arbitrary(classifier) { arb.bind() }.single() shouldBe 5
            arbitrary(shrinker, classifier) { arb.bind() }.single() shouldBe 5
            arbitrary(listOf(5)) { arb.bind() }.single() shouldBe 5
            arbitrary({ 5.asSample() }) { arb.bind() }.single() shouldBe 5
            arbitrary({ 5.asSample() }, shrinker) { arb.bind() }.single() shouldBe 5
         }
      }

      context("suspend arbitrary builder with unrestricted continuation") {
         fun combineAsString(vararg values: Any?): String = values.joinToString(" ")

         test("should build arb on the parent coroutine context") {
            val arb = withContext(Foo("hello")) {
               generateArbitrary {
                  val hello = coroutineContext[Foo]?.value
                  val world = arbitrary { "world" }.bind()
                  val first = Arb.int(1..10).bind()
                  val second = Arb.int(11..20).bind()
                  combineAsString(hello, world, first, second)
               }
            }

            arb.generate(RandomSource.seeded(1234L)).take(4).toList().map { it.value } shouldContainExactly listOf(
               "hello world 2 20",
               "hello world 6 12",
               "hello world 7 19",
               "hello world 9 13"
            )
         }

         test("should bind edgecases") {
            val arb: Arb<String> = generateArbitrary {
               val first = Arb.string(5, Codepoint.alphanumeric()).withEdgecases("edge1", "edge2").bind()
               val second = Arb.int(1..9).withEdgecases(5).bind()
               val third = Arb.int(101..109).withEdgecases(100 + second, 109).bind()
               combineAsString(first, second, third)
            }

            arb.edgecases() shouldContainExactlyInAnyOrder setOf(
               "edge1 5 105",
               "edge2 5 105",
               "edge1 5 109",
               "edge2 5 109",
            )
         }

         test("should preserve edgecases of dependent arbs, even when intermideary arb(s) have no edgecases") {

            val arb: Arb<String> = generateArbitrary {
               val first = Arb.string(5, Codepoint.alphanumeric()).withEdgecases("edge1", "edge2").bind()
               val second = Arb.int(1..4).withEdgecases(emptyList()).bind()
               val third = Arb.int(101..109).withEdgecases(100 + second).bind()
               combineAsString(first, second, third)
            }

            arb.edgecases() shouldContainExactlyInAnyOrder setOf(
               "edge1 1 101",
               "edge1 2 102",
               "edge1 3 103",
               "edge1 4 104",
               "edge2 1 101",
               "edge2 2 102",
               "edge2 3 103",
               "edge2 4 104"
            )
         }

         test("should propagate exception") {
            val throwingArb = generateArbitrary {
               val number = Arb.int(1..4).withEdgecases(emptyList()).bind()

               // try to throw something inside the arb
               number shouldBeGreaterThan 5
            }

            val assertionError = shouldThrow<AssertionError> { execute(RandomSource.seeded(1234L), throwingArb) }
            assertionError.message shouldBe "4 should be > 5"
         }

         test("should assign edgecases") {
            val edges = setOf("edge1", "edge2")
            val arb = generateArbitrary(edges.toList()) { "abcd" }

            arb.edgecases() shouldContainExactlyInAnyOrder edges
         }

         test("should assign edgecases and shrinker") {
            val shrinker = IntShrinker(1..5)
            val edges = setOf(1, 2)
            val arb = generateArbitrary(edges.toList(), shrinker) { 5 }

            arb.edgecases() shouldContainExactlyInAnyOrder edges
            arb.sample(RandomSource.seeded(1234L)).shrinks.children.value.map { it.value() } shouldBe shrinker.shrink(5)
         }

         test("should use shrinker when provided") {
            val shrinker = IntShrinker(1..5)
            val arb = generateArbitrary(shrinker) { 5 }

            arb.classifier.shouldBeNull()

            val shrinks = arb.sample(RandomSource.seeded(1234L)).shrinks
            shrinks.children.value.map { it.value() } shouldContainExactly shrinker.shrink(5)
         }

         test("should use classifier when provided") {
            val classifier = IntClassifier(1..5)
            val arb = generateArbitrary(classifier) { 5 }
            arb.classifier shouldBeSameInstanceAs classifier
         }

         test("should use classifier and shrinker when provided") {
            val shrinker = IntShrinker(1..5)
            val classifier = IntClassifier(1..5)
            val arb = generateArbitrary(shrinker, classifier) { 5 }

            arb.classifier shouldBeSameInstanceAs classifier

            val shrinks = arb.sample(RandomSource.seeded(1234L)).shrinks
            shrinks.children.value.map { it.value() } shouldContainExactly shrinker.shrink(5)
         }

         test("should use edgecase function when provided") {
            val arb = generateArbitrary({ 5.asSample() }) { 10 }
            arb.edgecases() shouldContainExactlyInAnyOrder setOf(5)
         }

         test("should use edgecase function and shrinker when provided") {
            val shrinker = IntShrinker(1..5)
            val arb = generateArbitrary({ 5.asSample() }, shrinker) { 10 }

            arb.edgecases() shouldContainExactlyInAnyOrder setOf(5)

            val shrinks = arb.sample(RandomSource.seeded(1234L)).shrinks
            shrinks.children.value.map { it.value() } shouldContainExactly shrinker.shrink(10)
         }

         test("should support .bind() syntax") {
            val arb = Arb.constant(5)
            val shrinker = IntShrinker(1..5)
            val classifier = IntClassifier(1..5)
            generateArbitrary { arb.bind() }.single() shouldBe 5
            generateArbitrary(shrinker) { arb.bind() }.single() shouldBe 5
            generateArbitrary(classifier) { arb.bind() }.single() shouldBe 5
            generateArbitrary(shrinker, classifier) { arb.bind() }.single() shouldBe 5
            generateArbitrary(listOf(5)) { arb.bind() }.single() shouldBe 5
            generateArbitrary({ 5.asSample() }) { arb.bind() }.single() shouldBe 5
            generateArbitrary({ 5.asSample() }, shrinker) { arb.bind() }.single() shouldBe 5
         }
      }
   }

   private data class Foo(val value: String) : CoroutineContext.Element {
      companion object : CoroutineContext.Key<Foo>

      override val key: CoroutineContext.Key<*> = Foo
   }

   private fun execute(rs: RandomSource, arb: Arb<*>): Unit {
      arb.generate(rs).take(1000).last()
   }
}

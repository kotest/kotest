package com.sksamuel.kotest.property.shrinking

import io.kotest.assertions.shouldFail
import io.kotest.assertions.withClue
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainOnlyDigits
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.RTree
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.ArbitraryBuilder
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.StringShrinkerWithMin
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.internal.doShrinking
import io.kotest.property.rtree

@EnabledIf(NotMacOnGithubCondition::class)
class StringShrinkerWithMinTest : DescribeSpec({

   beforeSpec {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   afterSpec {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

   describe("StringShrinkerWithMin") {
      it("should include bisected input") {
         checkAll { a: String ->
            if (a.length > 1) {
               val candidates = StringShrinkerWithMin().shrink(a)
               candidates.forAtLeastOne {
                  it.shouldHaveLength(a.length / 2 + a.length % 2)
               }
               candidates.forAtLeastOne {
                  it.shouldHaveLength(a.length / 2)
               }

            }
         }
      }

      it("should use first char to replace the second char") {
         StringShrinkerWithMin(4).shrink("atttt") shouldContain "aattt"
      }

      it("should replace last char with simplest") {
         StringShrinkerWithMin(4).shrink("atttt") shouldContain "attta"
      }

      it("should drop first char") {
         StringShrinkerWithMin(4).shrink("abcde") shouldContain "bcde"
      }

      it("should drop last char") {
         StringShrinkerWithMin(4).shrink("abcde") shouldContain "abcd"
      }

      it("should include first half variant") {
         StringShrinkerWithMin(1).shrink("abcdef") shouldContain "abc"
         StringShrinkerWithMin(1).shrink("abcde") shouldContain "abc"
      }

      it("should include second half variant") {
         StringShrinkerWithMin(1).shrink("abcdef") shouldContain "def"
         StringShrinkerWithMin(1).shrink("abcd") shouldContain "cd"
      }

      it("should shrink to expected value") {
         val prt = PropertyTesting.shouldPrintShrinkSteps
         PropertyTesting.shouldPrintShrinkSteps = false

         checkAll<String> { a ->

            val shrinks = StringShrinkerWithMin().rtree(a)
            val shrunk = doShrinking(shrinks, ShrinkingMode.Unbounded) {
               it.shouldNotContain("#")
            }

            if (a.contains("#")) {
               shrunk.shrink shouldBe "#"
            } else {
               shrunk.shrink shouldBe a
            }
         }

         PropertyTesting.shouldPrintShrinkSteps = prt
      }

      it("should prefer padded values") {
         val prt = PropertyTesting.shouldPrintShrinkSteps
         PropertyTesting.shouldPrintShrinkSteps = false

         val a = "97asd!@#ASD'''234)*safmasd"
         val shrinks = StringShrinkerWithMin().rtree(a)
         doShrinking(shrinks, ShrinkingMode.Unbounded) {
            it.length.shouldBeLessThan(3)
         }.shrink shouldBe "777"

         doShrinking(shrinks, ShrinkingMode.Unbounded) {
            it.length.shouldBeLessThan(8)
         }.shrink shouldBe "!!!!!!!!"

         PropertyTesting.shouldPrintShrinkSteps = prt
      }

      it("should respect min value") {
         val prt = PropertyTesting.shouldPrintShrinkSteps
         PropertyTesting.shouldPrintShrinkSteps = true
         val stdout = captureStandardOut {
            shouldFail {
               checkAll(PropTestConfig(seed = 123125), Arb.string(4, 8)) { a ->
                  // will cause the value to fail and shrinks be used, but nothing should be shrunk
                  // past the min value of 4, even though we fail on anything >= 2
                  a.shouldHaveLength(1)
               }
            }
         }

         stdout.shouldContain(
            """
Attempting to shrink arg "su{90) e"
Shrink #1: "su{9" fail
Shrink #2: "ss{9" fail
Shrink #3: "sss9" fail
Shrink #4: "ssss" fail
Shrink result (after 4 shrinks) => "ssss"
            """.trim()
         )
         PropertyTesting.shouldPrintShrinkSteps = prt
      }

      it("should generate samples that only contain numbers, given a numeric-codepoints Arb") {

         val numericChars = '0'..'9'
         val arbNumericCodepoints = Arb.of(numericChars.map { Codepoint(it.code) })
         val arbNumericString = Arb.string(1..10, arbNumericCodepoints)

         checkAll(arbNumericString) { numericString ->
            StringShrinkerWithMin()
               .shrink(numericString)
               .forAll { it.shouldContainOnlyDigits() }
         }
      }

      it("should only generate codepoint-compatible samples, when an Arb.string() is created") {

         val arbCharacters: Set<Char> = """  `!"£$%^&*()_+=-[]{}:@~;'#<>?,./  """.trim().toSet()
         val arbNumericCodepoints = Arb.of(arbCharacters.map { Codepoint(it.code) })
         val stringArb = Arb.string(minSize = 4, maxSize = 10, codepoints = arbNumericCodepoints)

         val arbSamples = arbitrary { rs -> stringArb.sample(rs) }

         checkAll(arbSamples) { sample ->
            withClue("all samples should only contain chars used to create the Arb.string(): $arbCharacters") {
               sample.value.toList().forAll { it.shouldBeIn(arbCharacters) }
               sample.shrinks.children.value.forAll { child: RTree<String> ->
                  child.value().toList().forAll { it.shouldBeIn(arbCharacters) }
               }
            }
         }
      }

      it("should generate simpler variants using simplestCharSelector") {

         val azStringArb = Arb.string(minSize = 10, maxSize = 10, Codepoint.az())
         val digitsArb = Arb.char('0'..'9')

         checkAll(azStringArb, digitsArb) { preShrinkString, digitChar ->

            val stringShrinker = StringShrinkerWithMin(minLength = 10) { digitChar }

            stringShrinker.shrink(preShrinkString).forAll { shrunkString ->
               shrunkString shouldHaveLength 10
               shrunkString shouldMatch Regex("[a-zA-Z$digitChar]{10}")
            }
         }
      }

      it("should not generate simpler variants when simplestCharSelector returns null") {

         val stringArb = Arb.string(minSize = 1, maxSize = 10, Codepoint.ascii())

         val stringShrinker = StringShrinkerWithMin(minLength = 1) { null }

         checkAll(stringArb) { preShrinkString ->
            stringShrinker.shrink(preShrinkString).forAll { shrunkString ->
               shrunkString shouldHaveLength shrunkString.length
            }
         }
      }

      it("should generate simpler variants using custom simplestCharSelector") {

         fun String.middleChar(): Char? = getOrNull(length / 2)
         val stringShrinker = StringShrinkerWithMin(minLength = 10) { it.middleChar() }

         val stringArbWithShrinker = ArbitraryBuilder.create {
            // '#' is the middle char - expect the '_' chars to be replaced by '#'
            "_____#_____"
         }.withShrinker(stringShrinker)
            .build()

         val prt = PropertyTesting.shouldPrintShrinkSteps
         PropertyTesting.shouldPrintShrinkSteps = true
         val stdout = captureStandardOut {
            shouldFail {
               checkAll(PropTestConfig(seed = 123125), stringArbWithShrinker) { a ->
                  // will cause the value to fail and shrinks be used, but nothing should be shrunk
                  // past the min value of 4, even though we fail on anything >= 2
                  a.shouldHaveLength(1)
               }
            }
         }

         stdout.shouldContain(
            """
Attempting to shrink arg "_____#_____"
Shrink #1: "_____#####" fail
Shrink #2: "#____#####" fail
Shrink #3: "##___#####" fail
Shrink #4: "###__#####" fail
Shrink #5: "####_#####" fail
Shrink #6: "##########" fail
Shrink result (after 6 shrinks) => "##########"
            """.trim()
         )
         PropertyTesting.shouldPrintShrinkSteps = prt
      }

      it("should generate simpler variants using dynamic simplestCharSelector") {

         // dynamically select the simplest char - it should be the char after the last space
         val stringShrinker = StringShrinkerWithMin(minLength = 10) {
            val lastSpaceCharIndex = it.indexOfLast { c -> c == ' ' }
            it.getOrNull(lastSpaceCharIndex + 1)
         }

         val stringArbWithShrinker = ArbitraryBuilder.create {
            "a b c d e f g h i j k"
         }.withShrinker(stringShrinker)
            .build()

         val prt = PropertyTesting.shouldPrintShrinkSteps
         PropertyTesting.shouldPrintShrinkSteps = true
         val stdout = captureStandardOut {
            shouldFail {
               checkAll(PropTestConfig(seed = 123125), stringArbWithShrinker) { a ->
                  // will cause the value to fail and shrinks be used, but nothing should be shrunk
                  // past the min value of 4, even though we fail on anything >= 2
                  a.shouldHaveLength(1)
               }
            }
         }

         stdout.shouldContain(
            """
Attempting to shrink arg "a b c d e f g h i j k"
Shrink #1: "a b c d e f" fail
Shrink #2: "a b c ffff" fail
Shrink #3: "f b c ffff" fail
Shrink #4: "ffb c ffff" fail
Shrink #5: "fff c ffff" fail
Shrink #6: "ffffc ffff" fail
Shrink #7: "fffff ffff" fail
Shrink #8: "ffffffffff" fail
Shrink result (after 8 shrinks) => "ffffffffff"
            """.trim()
         )
         PropertyTesting.shouldPrintShrinkSteps = prt
      }
   }
})

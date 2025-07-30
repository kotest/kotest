package io.kotest.datatest.styles

import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.datatest.FruitWithMemberNameCollision
import io.kotest.datatest.PythagTriple
import io.kotest.datatest.withData
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

@EnabledIf(LinuxOnlyGithubCondition::class)
class FeatureSpecDataTest : FeatureSpec() {
   init {

      duplicateTestNameMode = DuplicateTestNameMode.Silent

      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         count shouldBe 65
      }

      // test root level with varargs
      withData(
         PythagTriple(3, 4, 5),
         PythagTriple(6, 8, 10),
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // test root level with a sequence
      withData(
         sequenceOf(
            PythagTriple(8, 15, 17),
            PythagTriple(9, 12, 15),
            PythagTriple(15, 20, 25),
         )
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // test root level with an iterable
      withData(
         listOf(
            PythagTriple(8, 15, 17),
            PythagTriple(9, 12, 15),
            PythagTriple(15, 20, 25),
         )
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // testing repeated names get mangled
      var index = 0
      withData("a", "a", "a") {
         when (index) {
            0 -> this.testCase.name.name shouldBe "a"
            1 -> this.testCase.name.name shouldBe "(1) a"
            2 -> this.testCase.name.name shouldBe "(2) a"
         }
         index++
      }

      // tests mixing sequences and iterables and varargs
      withData("p", "q") { a ->
         withData(listOf("r", "s")) { b ->
            withData(sequenceOf("x", "y")) { c ->
               a + b + c shouldHaveLength 3
            }
         }
      }

      // handle collision between function name and property name
      withData(
         FruitWithMemberNameCollision("apple", 11),
         FruitWithMemberNameCollision("orange", 12),
      ) { (_, weight) ->
         weight shouldBeGreaterThan 10
      }

      // test we can define further context and tests inside a root level withData
      withData(
         "foo",
         "bar"
      ) {
         feature("feature $it") {
            scenario("scenario $it") {
               this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.FeatureSpecDataTest/$it -- feature $it -- scenario $it")
            }
         }
      }

      feature("inside a feature") {

         // test nested level with varargs
         withData(
            PythagTriple(3, 4, 5),
            PythagTriple(6, 8, 10),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         // test nested level with a sequence
         withData(
            sequenceOf(
               PythagTriple(8, 15, 17),
               PythagTriple(9, 12, 15),
               PythagTriple(15, 20, 25),
            )
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         // test nested level with an iterable
         withData(
            listOf(
               PythagTriple(8, 15, 17),
               PythagTriple(9, 12, 15),
               PythagTriple(15, 20, 25),
            )
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         // testing repeated names get mangled inside a context
         index = 0
         withData("a", "a", "a") {
            when (index) {
               0 -> this.testCase.name.name shouldBe "a"
               1 -> this.testCase.name.name shouldBe "(1) a"
               2 -> this.testCase.name.name shouldBe "(2) a"
            }
            index++
         }

         // tests mixing sequences and iterables and varargs inside a context
         withData("p", "q") { a ->
            withData(listOf("r", "s")) { b ->
               withData(sequenceOf("x", "y")) { c ->
                  a + b + c shouldHaveLength 3
               }
            }
         }

         // test we can define further context and tests inside a container level withData
         withData(
            "foo",
            "bar"
         ) {
            feature("feature $it") {
               scenario("scenario $it") {
                  this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.FeatureSpecDataTest/inside a feature -- $it -- feature $it -- scenario $it")
               }
            }
         }
      }
   }
}

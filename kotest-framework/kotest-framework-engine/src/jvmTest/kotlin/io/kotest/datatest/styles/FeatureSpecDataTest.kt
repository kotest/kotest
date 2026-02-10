package io.kotest.datatest.styles

import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.datatest.FruitWithMemberNameCollision
import io.kotest.datatest.PythagTriple
import io.kotest.datatest.withFeatures
import io.kotest.datatest.withScenarios
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

@EnabledIf(LinuxOnlyGithubCondition::class)
class FeatureSpecDataTest : FeatureSpec() {
   init {

      duplicateTestNameMode = DuplicateTestNameMode.Silent

      var beforeAnyCounter = 0
      var beforeEachCounter = 0
      var beforeTestCounter = 0
      var afterTestCounter = 0
      beforeAny {
         beforeAnyCounter++
      }
      beforeEach {
         beforeEachCounter++
      }
      beforeTest {
         beforeTestCounter++
      }
      afterTest {
         afterTestCounter++
      }

      afterSpec {
         afterTestCounter shouldBe 111
         beforeAnyCounter shouldBe 111
         beforeEachCounter shouldBe 36
         beforeTestCounter shouldBe 111
      }

      // test root level with varargs
      withFeatures(
         PythagTriple(3, 4, 5),
         PythagTriple(6, 8, 10),
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // test root level with a sequence
      withFeatures(
         sequenceOf(
            PythagTriple(8, 15, 17),
            PythagTriple(9, 12, 15),
            PythagTriple(15, 20, 25),
         )
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // test root level with an iterable
      withFeatures(
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
      withFeatures("a", "a", "a") {
         when (index) {
            0 -> this.testCase.name.name shouldBe "a"
            1 -> this.testCase.name.name shouldBe "(1) a"
            2 -> this.testCase.name.name shouldBe "(2) a"
         }
         index++
      }

      // tests mixing sequences and iterables and varargs
      withFeatures("p", "q") { a ->
         withFeatures(listOf("r", "s")) { b ->
            withFeatures(sequenceOf("x", "y")) { c ->
               a + b + c shouldHaveLength 3
            }
         }
      }

      // handle collision between function name and property name
      withFeatures(
         FruitWithMemberNameCollision("apple", 11),
         FruitWithMemberNameCollision("orange", 12),
      ) { (_, weight) ->
         weight shouldBeGreaterThan 10
      }

      // test we can define further context and tests inside a root level withFeatures
      withFeatures(
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
         withFeatures(
            PythagTriple(3, 4, 5),
            PythagTriple(6, 8, 10),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         // test nested level with a sequence
         withFeatures(
            sequenceOf(
               PythagTriple(8, 15, 17),
               PythagTriple(9, 12, 15),
               PythagTriple(15, 20, 25),
            )
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         // test nested level with an iterable
         withFeatures(
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
         withFeatures("a", "a", "a") {
            when (index) {
               0 -> this.testCase.name.name shouldBe "a"
               1 -> this.testCase.name.name shouldBe "(1) a"
               2 -> this.testCase.name.name shouldBe "(2) a"
            }
            index++
         }

         // tests mixing sequences and iterables and varargs inside a context
         withFeatures("p", "q") { a ->
            withFeatures(listOf("r", "s")) { b ->
               withFeatures(sequenceOf("x", "y")) { c ->
                  a + b + c shouldHaveLength 3
               }
            }
         }

         // test we can define further context and tests inside a container level withFeatures
         withFeatures(
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

      // nesting all new WithXXX
      withFeatures("a", "b") { a ->
         withFeatures("a", "b") { b ->
            withFeatures("a", "b") { c ->
               withScenarios("scen1", "scen2") { d ->
                  a + b + c + d  shouldHaveLength 8
               }
               withScenarios("scen3", "scen4") { e ->
                  a + b + c + e  shouldHaveLength 8
               }
            }
         }
      }
   }
}

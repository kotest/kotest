package io.kotest.datatest.styles

import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.datatest.FruitWithMemberNameCollision
import io.kotest.datatest.PythagTriple
import io.kotest.datatest.withShoulds
import io.kotest.datatest.withWhens
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class WordSpecDataTest : WordSpec() {
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
         afterTestCounter shouldBe 146
         beforeAnyCounter shouldBe 146
         // only counting tests within the shoulds
         beforeEachCounter shouldBe 36
         beforeTestCounter shouldBe 146
      }

      // test root level with varargs
      withWhens(
         PythagTriple(3, 4, 5),
         PythagTriple(6, 8, 10),
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // test root level with a sequence
      withWhens(
         sequenceOf(
            PythagTriple(8, 15, 17),
            PythagTriple(9, 12, 15),
            PythagTriple(15, 20, 25),
         )
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // test root level with an iterable
      withWhens(
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
      withWhens("a", "a", "a") {
         when (index) {
            0 -> this.testCase.name.name shouldBe "a"
            1 -> this.testCase.name.name shouldBe "(1) a"
            2 -> this.testCase.name.name shouldBe "(2) a"
         }
         index++
      }

      // tests mixing sequences and iterables and varargs
      withWhens("p", "q") { a ->
         withWhens(listOf("r", "s")) { b ->
            withWhens(sequenceOf("x", "y")) { c ->
               a + b + c shouldHaveLength 3
            }
         }
      }

      // handle collision between function name and property name
      withWhens(
         FruitWithMemberNameCollision("apple", 11),
         FruitWithMemberNameCollision("orange", 12),
      ) { (_, weight) ->
         weight shouldBeGreaterThan 10
      }

      // test we can define further context and tests inside a root level withWhens
      withWhens(
         "foo",
         "bar"
      ) {
         "context $it" should {
            "test $it" {
               this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.WordSpecDataTest/$it -- context $it -- test $it")
            }
         }
      }

      "inside a context" `when` {

         // test nested level with varargs
         withWhens(
            PythagTriple(3, 4, 5),
            PythagTriple(6, 8, 10),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         // test nested level with a sequence
         withWhens(
            sequenceOf(
               PythagTriple(8, 15, 17),
               PythagTriple(9, 12, 15),
               PythagTriple(15, 20, 25),
            )
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         // test nested level with an iterable
         withWhens(
            listOf(
               PythagTriple(8, 15, 17),
               PythagTriple(9, 12, 15),
               PythagTriple(15, 20, 25),
            )
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         withWhens(
            mapOf(
               "true" to true,
               "false" to false,
               "null" to null,
            )
         ) { _ ->
         }

         // testing repeated names get mangled inside a context
         index = 0
         withWhens("a", "a", "a") {
            when (index) {
               0 -> this.testCase.name.name shouldBe "a"
               1 -> this.testCase.name.name shouldBe "(1) a"
               2 -> this.testCase.name.name shouldBe "(2) a"
            }
            index++
         }

         // tests mixing sequences and iterables and varargs inside a context
         withWhens("p", "q") { a ->
            withWhens(listOf("r", "s")) { b ->
               withWhens(sequenceOf("x", "y")) { c ->
                  a + b + c shouldHaveLength 3
               }
            }
         }

         // test we can define further context and tests inside a container level withWhens
         withWhens(
            "foo",
            "bar"
         ) {
            "context $it" should {
               "test $it" {
                  this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.WordSpecDataTest/inside a context -- $it -- context $it -- test $it")
               }
            }
         }
      }

      // nesting all new WithXXX
      withWhens("a", "b") { a ->
         withWhens("a", "b") { b ->
            withWhens("a", "b") { c ->
               withShoulds("should1", "should2") { d ->
                  "test $d" {
                     a + b + c + d shouldHaveLength 10
                  }
               }
               withShoulds("should3", "should4") { e ->
                  "test $e" {
                     a + b + c + e shouldHaveLength 10
                  }
               }
            }
         }
      }
   }
}

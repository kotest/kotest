package io.kotest.datatest.styles

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.FruitWithMemberNameCollision
import io.kotest.datatest.PythagTriple
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class FunSpecDataTest : FunSpec() {
   init {

      duplicateTestNameMode = DuplicateTestNameMode.Silent

      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         count shouldBe 68
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
            0 -> this.testCase.name.testName shouldBe "a"
            1 -> this.testCase.name.testName shouldBe "(1) a"
            2 -> this.testCase.name.testName shouldBe "(2) a"
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
         context("context $it") {
            test("test $it") {
               this.testCase.descriptor.path(includeSpec = false).value shouldBe "$it -- context $it -- test $it"
            }
         }
      }

      context("inside a context") {

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

         withData(
            mapOf(
               "true" to true,
               "false" to false,
               "null" to null,
            )
         ) { _ ->
         }

         // testing repeated names get mangled inside a context
         index = 0
         withData("a", "a", "a") {
            when (index) {
               0 -> this.testCase.name.testName shouldBe "a"
               1 -> this.testCase.name.testName shouldBe "(1) a"
               2 -> this.testCase.name.testName shouldBe "(2) a"
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
            context("context $it") {
               test("test $it") {
                  this.testCase.descriptor.path(includeSpec = false).value shouldBe "inside a context -- $it -- context $it -- test $it"
               }
            }
         }
      }
   }
}

package io.kotest.datatest.styles

import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.FruitWithMemberNameCollision
import io.kotest.datatest.PythagTriple
import io.kotest.datatest.withContexts
import io.kotest.datatest.withTests
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
         count shouldBe 114
      }

      // test root level with varargs
      withContexts(
         PythagTriple(3, 4, 5),
         PythagTriple(6, 8, 10),
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // test root level with a sequence
      withContexts(
         sequenceOf(
            PythagTriple(8, 15, 17),
            PythagTriple(9, 12, 15),
            PythagTriple(15, 20, 25),
         )
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // test root level with an iterable
      withContexts(
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
      withContexts("a", "a", "a") {
         when (index) {
            0 -> this.testCase.name.name shouldBe "a"
            1 -> this.testCase.name.name shouldBe "(1) a"
            2 -> this.testCase.name.name shouldBe "(2) a"
         }
         index++
      }

      // tests mixing sequences and iterables and varargs
      withContexts("p", "q") { a ->
         withContexts(listOf("r", "s")) { b ->
            withContexts(sequenceOf("x", "y")) { c ->
               a + b + c shouldHaveLength 3
            }
         }
      }

      // handle collision between function name and property name
      withContexts(
         FruitWithMemberNameCollision("apple", 11),
         FruitWithMemberNameCollision("orange", 12),
      ) { (_, weight) ->
         weight shouldBeGreaterThan 10
      }

      // test we can define further context and tests inside a root level withContexts
      withContexts(
         "foo",
         "bar"
      ) {
         context("context $it") {
            test("test $it") {
               this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.FunSpecDataTest/$it -- context $it -- test $it")
            }
         }
      }

      context("inside a context") {

         // test nested level with varargs
         withContexts(
            PythagTriple(3, 4, 5),
            PythagTriple(6, 8, 10),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         // test nested level with a sequence
         withContexts(
            sequenceOf(
               PythagTriple(8, 15, 17),
               PythagTriple(9, 12, 15),
               PythagTriple(15, 20, 25),
            )
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         // test nested level with an iterable
         withContexts(
            listOf(
               PythagTriple(8, 15, 17),
               PythagTriple(9, 12, 15),
               PythagTriple(15, 20, 25),
            )
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         withContexts(
            mapOf(
               "true" to true,
               "false" to false,
               "null" to null,
            )
         ) { _ ->
         }

         // testing repeated names get mangled inside a context
         index = 0
         withContexts("a", "a", "a") {
            when (index) {
               0 -> this.testCase.name.name shouldBe "a"
               1 -> this.testCase.name.name shouldBe "(1) a"
               2 -> this.testCase.name.name shouldBe "(2) a"
            }
            index++
         }

         // tests mixing sequences and iterables and varargs inside a context
         withContexts("p", "q") { a ->
            withContexts(listOf("r", "s")) { b ->
               withContexts(sequenceOf("x", "y")) { c ->
                  a + b + c shouldHaveLength 3
               }
            }
         }

         // test we can define further context and tests inside a container level withContexts
         withContexts(
            "foo",
            "bar"
         ) {
            context("context $it") {
               test("test $it") {
                  this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.FunSpecDataTest/inside a context -- $it -- context $it -- test $it")
               }
            }
         }
      }

      // nesting all new WithXXX
      withContexts("a", "b") { a ->
         withContexts("a", "b") { b ->
            withContexts("a", "b") { c ->
               withTests("test1", "test2") { d ->
                  a + b + c + d shouldHaveLength 8
               }
               withTests("test3", "test4") { e ->
                  a + b + c + e shouldHaveLength 8
               }
            }
         }
      }
   }
}

package io.kotest.datatest.styles

import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.FruitWithMemberNameCollision
import io.kotest.datatest.PythagTriple
import io.kotest.datatest.withContexts
import io.kotest.datatest.withShoulds
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class ShouldSpecDataTest : ShouldSpec() {
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
         afterTestCounter shouldBe 114
         beforeAnyCounter shouldBe 114
         beforeEachCounter shouldBe 36
         beforeTestCounter shouldBe 114
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
            should("should $it") {
               this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.ShouldSpecDataTest/$it -- context $it -- should $it")
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
               should("should $it") {
                  this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.ShouldSpecDataTest/inside a context -- $it -- context $it -- should $it")
               }
            }
         }
      }

      // nesting all new WithXXX
      withContexts("a", "b") { a ->
         withContexts("a", "b") { b ->
            withContexts("a", "b") { c ->
               withShoulds("should1", "should2") { d ->
                  a + b + c + d  shouldHaveLength 10
               }
               withShoulds("should3", "should4") { e ->
                  a + b + c + e  shouldHaveLength 10
               }
            }
         }
      }
   }
}

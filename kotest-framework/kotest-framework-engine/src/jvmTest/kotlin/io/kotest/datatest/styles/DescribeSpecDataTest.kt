package io.kotest.datatest.styles

import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.FruitWithMemberNameCollision
import io.kotest.datatest.PythagTriple
import io.kotest.datatest.withContexts
import io.kotest.datatest.withDescribes
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class DescribeSpecDataTest : DescribeSpec() {
   init {

      duplicateTestNameMode = DuplicateTestNameMode.Silent

      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         count shouldBe 153
      }

      // test root level with varargs
      withContexts(
         PythagTriple(3, 4, 5),
         PythagTriple(6, 8, 10),
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // test root level with varargs
      withDescribes(
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

      // test root level with a sequence
      withDescribes(
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

      // test root level with an iterable
      withDescribes(
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

      // testing repeated names get mangled
      index = 0
      withDescribes("a", "a", "a") {
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

      // tests mixing sequences and iterables and varargs
      withDescribes("p", "q") { a ->
         withDescribes(listOf("r", "s")) { b ->
            withDescribes(sequenceOf("x", "y")) { c ->
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

      // handle collision between function name and property name
      withDescribes(
         FruitWithMemberNameCollision("apple", 11),
         FruitWithMemberNameCollision("orange", 12),
      ) { (_, weight) ->
         weight shouldBeGreaterThan 10
      }

      // test we can define further context and tests inside a root level withContexts
      withContexts(
         "foo1",
         "bar1"
      ) {
         context("describe $it") {
            it(it) {
               this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.DescribeSpecDataTest/$it -- describe $it -- $it")
            }
         }
      }

      // test we can define further describes and tests inside a root level withContexts
      withContexts(
         "foo2",
         "bar2"
      ) {
         describe("describe $it") {
            it(it) {
               this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.DescribeSpecDataTest/$it -- describe $it -- $it")
            }
         }
      }

      // test we can define further contexts and tests inside a root level withDescribes
      withDescribes(
         "foo3",
         "bar3"
      ) {
         context("describe $it") {
            it(it) {
               this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.DescribeSpecDataTest/$it -- describe $it -- $it")
            }
         }
      }

      // test we can define further describes and tests inside a root level withDescribes
      withDescribes(
         "foo4",
         "bar4"
      ) {
         describe("describe $it") {
            it(it) {
               this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.DescribeSpecDataTest/$it -- describe $it -- $it")
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

         // test nested level with varargs
         withDescribes(
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

         // test nested level with a sequence
         withDescribes(
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

         // test nested level with an iterable
         withDescribes(
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
         withContexts("a", "a", "a") {
            when (index) {
               0 -> this.testCase.name.name shouldBe "a"
               1 -> this.testCase.name.name shouldBe "(1) a"
               2 -> this.testCase.name.name shouldBe "(2) a"
            }
            index++
         }

         // testing repeated names get mangled inside a describe
         index = 0
         withDescribes("b", "b", "b") {
            when (index) {
               0 -> this.testCase.name.name shouldBe "b"
               1 -> this.testCase.name.name shouldBe "(1) b"
               2 -> this.testCase.name.name shouldBe "(2) b"
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

         // tests mixing sequences and iterables and varargs inside a describe
         withDescribes("p", "q") { a ->
            withDescribes(listOf("r", "s")) { b ->
               withDescribes(sequenceOf("x", "y")) { c ->
                  a + b + c shouldHaveLength 3
               }
            }
         }

         // test we can define further describe and tests inside a container level withContexts
         withContexts(
            "foo1",
            "bar1"
         ) {
            describe("describe $it") {
               it(it) {
                  this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.DescribeSpecDataTest/inside a context -- $it -- describe $it -- $it")
               }
            }
         }

         // test we can define further context and tests inside a container level withContexts
         withContexts(
            "foo2",
            "bar2"
         ) {
            context("describe $it") {
               it(it) {
                  this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.DescribeSpecDataTest/inside a context -- $it -- describe $it -- $it")
               }
            }
         }

         // test we can define further describe and tests inside a container level withDescribes
         withDescribes(
            "foo3",
            "bar3"
         ) {
            describe("describe $it") {
               it(it) {
                  this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.DescribeSpecDataTest/inside a context -- $it -- describe $it -- $it")
               }
            }
         }

         // test we can define further context and tests inside a container level withDescribes
         withDescribes(
            "foo4",
            "bar4"
         ) {
            context("describe $it") {
               it(it) {
                  this.testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.DescribeSpecDataTest/inside a context -- $it -- describe $it -- $it")
               }
            }
         }
      }
   }
}

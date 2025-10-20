package io.kotest.datatest.styles

import io.kotest.assertions.assertSoftly
import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.datatest.FruitWithMemberNameCollision
import io.kotest.datatest.PythagTriple
import io.kotest.datatest.withAnds
import io.kotest.datatest.withContexts
import io.kotest.datatest.withGivens
import io.kotest.datatest.withThens
import io.kotest.datatest.withWhens
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class BehaviorSpecDataTest : BehaviorSpec() {
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
        afterTestCounter shouldBe 151
        beforeAnyCounter shouldBe 151
        beforeEachCounter shouldBe 42
        beforeTestCounter shouldBe 151
     }


    // test root level with varargs
     withContexts(
        PythagTriple(3, 4, 5),
        PythagTriple(6, 8, 10)) { (a, b, c) ->
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
           0 -> testCase.name.name shouldBe "a"
           1 -> testCase.name.name shouldBe "(1) a"
           2 -> testCase.name.name shouldBe "(2) a"
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
        FruitWithMemberNameCollision("orange", 12)
     ) { (_, weight) ->
        weight shouldBeGreaterThan 10
     }

     // test we can define further given and whens inside a root level withData
     withContexts("foo", "bar") {
        given("given $it") {
           `when`("when $it") {
              then("then $it") {
                 testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.BehaviorSpecDataTest/$it -- given $it -- when $it -- then $it")
              }
           }
        }
     }

     given("inside a given") {

      // test nested level with varargs
        withAnds(
           PythagTriple(3, 4, 5),
           PythagTriple(6, 8, 10)) { (a, b, c) ->
           a * a + b * b shouldBe c * c
        }

        // test nested level with a sequence
        withAnds(
           sequenceOf(
              PythagTriple(8, 15, 17),
              PythagTriple(9, 12, 15),
              PythagTriple(15, 20, 25),
           )
        ) { (a, b, c) ->
           a * a + b * b shouldBe c * c
        }

        // test nested level with an iterable
        withAnds(
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
        withAnds("a", "a", "a") {
           when (index) {
              0 -> testCase.name.name shouldBe "a"
              1 -> testCase.name.name shouldBe "(1) a"
              2 -> testCase.name.name shouldBe "(2) a"
           }
           index++
        }

        // tests mixing sequences and iterables and varargs inside a context
        withAnds("p", "q") { a ->
           withAnds(listOf("r", "s")) { b ->
              withAnds(sequenceOf("x", "y")) { c ->
                 a + b + c shouldHaveLength 3
              }
           }
        }


        // test we can define further tests at each level
        withAnds("foo", "bar") { a ->
           given("given $a") {
              withAnds("foo", "bar") { b ->
                 `when`("when $b") {
                    withAnds("foo", "bar") { c ->
                       then("then $c") {
                          testCase.descriptor.path() shouldBe DescriptorPath("io.kotest.datatest.styles.BehaviorSpecDataTest/inside a given -- $a -- given $a -- $b -- when $b -- $c -- then $c")
                       }
                    }
                 }
              }
           }
        }
     }

     // nesting all new WithXXX
     withContexts("a", "b") { a ->
        withGivens("a", "b") { b ->
           withAnds("a", "b") { c ->
              withWhens("a", "b") { d ->
                 withThens("a", "b") { e ->
                    a + b + c + d + e  shouldHaveLength 5
                 }
              }
           }
        }
     }
  }
}

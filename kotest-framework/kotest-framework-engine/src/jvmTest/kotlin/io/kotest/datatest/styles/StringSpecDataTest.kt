package io.kotest.datatest.styles

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.FruitWithMemberNameCollision
import io.kotest.datatest.PythagTriple
import io.kotest.datatest.withData
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class StringSpecDataTest : StringSpec() {
   init {

      duplicateTestNameMode = DuplicateTestNameMode.Silent

      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         count shouldBe 19
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

      // tests with varargs
      withData("p", "q") { a ->
         a shouldHaveLength 1
      }

      // tests with sequences
      withData(listOf("r", "s")) { a ->
         a shouldHaveLength 1
      }

      // tests with iterables
      withData(sequenceOf("x", "y")) { a ->
         a shouldHaveLength 1
      }


      // handle collision between function name and property name
      withData(
         FruitWithMemberNameCollision("apple", 11),
         FruitWithMemberNameCollision("orange", 12),
      ) { (_, weight) ->
         weight shouldBeGreaterThan 10
      }
   }
}

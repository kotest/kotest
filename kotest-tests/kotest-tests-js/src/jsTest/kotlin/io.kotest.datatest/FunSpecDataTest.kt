package io.kotest.datatest

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecDataTest : FunSpec() {
   init {
      duplicateTestNameMode = DuplicateTestNameMode.Silent

      // varargs
      withData(
         PythagTriple(3, 4, 5),
         PythagTriple(6, 8, 10),
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // using sequences
      withData(
         sequenceOf(
            PythagTriple(8, 15, 17),
            PythagTriple(9, 12, 15),
            PythagTriple(15, 20, 25),
         )
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // name function
      withData<PythagTriple>(
         { "For pythag triple: ${it.a}^2 * ${it.b}^2 = ${it.c}^2" },
         PythagTriple(3, 4, 5),
         PythagTriple(6, 8, 10),
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
         if (a == 3) this.testCase.name.testName shouldBe "For pythag triple: 3^2 * 4^2 = 5^2"
         if (a == 6) this.testCase.name.testName shouldBe "For pythag triple: 6^2 * 8^2 = 10^2"
      }

      // map of names
      withData(
         mapOf(
            "foo" to 2,
            "bar" to 4,
         )
      ) { a ->
         a % 2 shouldBe 0
         if (a == 2) this.testCase.name.testName shouldBe "foo"
         if (a == 4) this.testCase.name.testName shouldBe "bar"
      }
   }
}

data class PythagTriple(val a: Int, val b: Int, val c: Int)

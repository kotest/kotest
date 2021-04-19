package io.kotest.datatest

import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.test.TestContext
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

data class PythagTriple(val a: Int, val b: Int, val c: Int)

fun RootScope.registerRootTests(): MutableList<String> {

   val results = mutableListOf<String>()

   forAll(
      PythagTriple(3, 4, 5),
      PythagTriple(6, 8, 10),
   ) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   forAll(
      PythagTriple(8, 15, 17),
      PythagTriple(9, 12, 15),
      PythagTriple(15, 20, 25),
   ) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   forAll("a", "b") { a->
      forAll(sequenceOf("x", "y")) { b ->
         a + b shouldHaveLength 2
         results.add(a + b)
      }
   }

   // testing repeated names get mapped
   forAll("a", "b") { a ->
      forAll(sequenceOf("x", "y")) { b ->
         a + b shouldHaveLength 2
         results.add(a + b)
      }
   }

   forAll("p", "q") { a ->
      forAll(listOf("r", "s")) { b ->
         forAll(sequenceOf("x", "y")) { c ->
            a + b + c shouldHaveLength 3
            results.add(a + b + c)
         }
      }
   }

   return results
}

suspend fun TestContext.registerContextTests(): MutableList<String> {

   val results = mutableListOf<String>()

   forAll(
      PythagTriple(3, 4, 5),
      PythagTriple(6, 8, 10),
   ) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   forAll(
      PythagTriple(8, 15, 17),
      PythagTriple(9, 12, 15),
      PythagTriple(15, 20, 25),
   ) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   forAll("a", "b") { a ->
      forAll(sequenceOf("x", "y")) { b ->
         a + b shouldHaveLength 2
         results.add(a + b)
      }
   }

   // testing repeated names get mapped
   forAll("a", "b") { a ->
      forAll(sequenceOf("x", "y")) { b ->
         a + b shouldHaveLength 2
         results.add(a + b)
      }
   }

   forAll("p", "q") { a ->
      forAll(listOf("r", "s")) { b ->
         forAll(sequenceOf("x", "y")) { c ->
            a + b + c shouldHaveLength 3
            results.add(a + b + c)
         }
      }
   }

   return results
}

fun List<String>.assertDataTestResults() {
   shouldContainExactly(
      "ax",
      "ay",
      "bx",
      "by",
      "ax",
      "ay",
      "bx",
      "by",
      "prx",
      "pry",
      "psx",
      "psy",
      "qrx",
      "qry",
      "qsx",
      "qsy",
   )
}

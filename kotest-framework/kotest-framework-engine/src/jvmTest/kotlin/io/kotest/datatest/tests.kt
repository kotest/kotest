package io.kotest.datatest

import io.kotest.engine.datatest.withData
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

data class PythagTriple(val a: Int, val b: Int, val c: Int)

internal data class FruitWithMemberNameCollision(
   val name: String,
   val weight: Int
) {
   fun weight() = 42
}

@ExperimentalKotest
fun RootScope.registerRootTests(): MutableList<String> {

   val results = mutableListOf<String>()

   withData(
      PythagTriple(3, 4, 5),
      PythagTriple(6, 8, 10),
   ) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   withData(
      sequenceOf(
         PythagTriple(8, 15, 17),
         PythagTriple(9, 12, 15),
         PythagTriple(15, 20, 25),
      )
   ) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   withData(
      mapOf(
         "foo" to 2,
         "foo" to 4,
      )
   ) { a ->
      a % 2 shouldBe 0
      if (a == 2) this.testCase.name.testName shouldBe "foo"
   }

   withData("a", "b") { a ->
      withData(sequenceOf("x", "y")) { b ->
         a + b shouldHaveLength 2
         results.add(a + b)
      }
   }

   // testing repeated names get mangled
   withData("a", "b") { a ->
      withData(sequenceOf("x", "y")) { b ->
         a + b shouldHaveLength 2
         results.add(a + b)
      }
   }

   // we already had a / b so the names should be mangled
   withData(
      mapOf(
         "a" to 2,
         "b" to 4,
      )
   ) { arg ->
      arg % 2 shouldBe 0
      if (arg == 2) this.testCase.name.testName shouldBe "(2) a"
      if (arg == 4) this.testCase.name.testName shouldBe "(2) b"
   }

   withData("p", "q") { a ->
      withData(listOf("r", "s")) { b ->
         withData(sequenceOf("x", "y")) { c ->
            a + b + c shouldHaveLength 3
            results.add(a + b + c)
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

   return results
}

@ExperimentalKotest
suspend fun ContainerScope.registerContextTests(): MutableList<String> {

   val results = mutableListOf<String>()

   withData(
      PythagTriple(3, 4, 5),
      PythagTriple(6, 8, 10),
   ) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   withData(
      PythagTriple(8, 15, 17),
      PythagTriple(9, 12, 15),
      PythagTriple(15, 20, 25),
   ) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   withData(
      mapOf(
         "foo" to 2,
         "foo" to 4,
      )
   ) { a ->
      a % 2 shouldBe 0
      if (a == 2) this.testCase.name.testName shouldBe "foo"
   }

   withData("a", "b") { a ->
      withData(sequenceOf("x", "y")) { b ->
         a + b shouldHaveLength 2
         results.add(a + b)
      }
   }

   // testing repeated names get mapped
   withData("a", "b") { a ->
      withData(sequenceOf("x", "y")) { b ->
         a + b shouldHaveLength 2
         results.add(a + b)
      }
   }

   // we already had a / b so the names should be mangled
   withData(
      mapOf(
         "a" to 2,
         "b" to 4,
      )
   ) { arg ->
      arg % 2 shouldBe 0
      if (arg == 2) this.testCase.name.testName shouldBe "(2) a"
      if (arg == 4) this.testCase.name.testName shouldBe "(2) b"
   }

   withData("p", "q") { a ->
      withData(listOf("r", "s")) { b ->
         withData(sequenceOf("x", "y")) { c ->
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

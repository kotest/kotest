package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.core.listeners.FinishSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import kotlin.reflect.KClass

@ExperimentalKotest
class FunSpecDataTest : FunSpec() {
   init {

      configuration.registerExtension(object : FinishSpecListener {
         override suspend fun finishSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
            results.keys.map { it.displayName }.toSet() shouldBe setOf(
               "(1) a",
               "x",
               "y",
               "(1) b",
               "a",
               "x",
               "y",
               "b",
               "x",
               "y",
               "context with rename function",
               "qPythagTriple(a=3, b=4, c=5)",
               "qPythagTriple(a=6, b=8, c=10)",
               "context with sequences and lists",
               "inside a context",
               "PythagTriple(a=3, b=4, c=5)",
               "PythagTriple(a=6, b=8, c=10)",
               "PythagTriple(a=8, b=15, c=17)",
               "PythagTriple(a=9, b=12, c=15)",
               "PythagTriple(a=15, b=20, c=25)",
               "qPythagTriple(a=3, b=4, c=5)",
               "qPythagTriple(a=6, b=8, c=10)",
            )
         }
      })

      // root level test
      withData(
         PythagTriple(3, 4, 5),
         PythagTriple(6, 8, 10),
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // root with sequence
      withData(
         sequenceOf(
            PythagTriple(8, 15, 17),
            PythagTriple(9, 12, 15),
            PythagTriple(15, 20, 25),
         )
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      // nested root
      withData("a", "b") { a ->
         withData(sequenceOf("x", "y")) { b ->
            a + b shouldHaveLength 2
         }
      }

      // we already had a / b so the names should be mangled
      withData("a", "b") { a ->
         withData(sequenceOf("x", "y")) { b ->
            a + b shouldHaveLength 2
         }
      }

      // root with rename function
      withData<PythagTriple>(
         { "q$it" },
         PythagTriple(3, 4, 5),
         PythagTriple(6, 8, 10),
      ) { (a, b, c) ->
         a * a + b * b shouldBe c * c
      }

      context("inside a context") {
         withData(
            PythagTriple(3, 4, 5),
            PythagTriple(6, 8, 10),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }

         context("inside a nested context") {
            withData(
               PythagTriple(3, 4, 5),
               PythagTriple(6, 8, 10),
            ) { (a, b, c) ->
               a * a + b * b shouldBe c * c
            }
         }
      }

      context("context with sequences and lists") {
         withData("p", "q") { a ->
            withData(listOf("r", "s")) { b ->
               withData(sequenceOf("x", "y")) { c ->
                  a + b + c shouldHaveLength 3
               }
            }
         }
      }

      context("context with rename function") {
         // root with rename function
         withData<PythagTriple>(
            { "q$it" },
            PythagTriple(3, 4, 5),
            PythagTriple(6, 8, 10),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }
   }
}

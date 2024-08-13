package io.kotest.property.core

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalKotest
class PermutationContextElement(
   val context: PermutationContext,
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<PermutationContextElement>
}

@ExperimentalKotest
fun TestScope.permutationConfig(configure: PermutationContext.() -> Unit) {
   // would need to add this from the coroutineContext, but that is immutable
   // so we need to extend kotest framework with a mutable map of elements that can change at runtime and put it there
   val context = PermutationContext()
   context.configure()
}

@ExperimentalKotest
suspend fun TestScope.withPermutation(test: suspend PermutationContext.() -> Unit) {
   val context = coroutineContext[PermutationContextElement]?.context ?: error("PermutationContextElement not found")
   executePropTest(context, test)
}

@ExperimentalKotest
fun TestScope.beforePermutation(fn: suspend () -> Unit) {
   val context = coroutineContext[PermutationContextElement]?.context ?: error("PermutationContextElement not found")
   context.beforePermutation = fn
}

@ExperimentalKotest
fun TestScope.afterPermutation(fn: suspend () -> Unit) {
   val context = coroutineContext[PermutationContextElement]?.context ?: error("PermutationContextElement not found")
   context.afterPermutation = fn
}

class DelegateTest : FunSpec() {
   init {

      test("property syntax 1") {

         permutationConfig {
            iterations = 42 // run this property test 42 times
            failOnSeed = true // will fail if a seed is set
            edgecaseFactor = 0.1 // 10% of generated values will be edgecases
            shouldPrintGeneratedValues = true // output each generated value
         }

         withPermutation {
            val a by gen { Arb.int() }
            val b by gen { Arb.int() }

            a + b shouldBe a + b
         }
      }

      test("delegate syntax 1") {

         permutationConfig {
            iterations = 42 // run this property test 42 times
            failOnSeed = true // will fail if a seed is set
            edgecaseFactor = 0.1 // 10% of generated values will be edgecases
            shouldPrintGeneratedValues = true // output each generated value
         }

         withPermutation {
            val a by gen { Arb.int() }
            val b by gen { Arb.int() }

            a + b shouldBe a + b
         }
      }

      test("delegate syntax 2") {

         permutationConfig {
            duration = 100.milliseconds // run this property test for as many iteration as 100ms allows
            minSuccess = 5 // require 5 successful tests to ensure we get at least some in a duration based test
            maxFailure = 1 // allow 1 failure for a flakey test
            shrinkingMode = ShrinkingMode.Off // disable shrinking on this prop test
         }

         withPermutation {

            val first by gen { Arb.string() }
            val second by gen { Arb.string() }

            val concat = first + second

            concat shouldStartWith first
            concat shouldEndWith second
            concat shouldHaveLength (first.length + second.length)
         }
      }

      test("shrinking") {

         permutationConfig {
            seed = 838127382173 // override seed
            shouldPrintShrinkSteps = true // print shrink steps
         }

         withPermutation {
            val a by gen { Arb.int() }
            val b by gen { Arb.int() }

            a * b shouldBe a + b
         }
      }

      test("assumptions") {

         withPermutation {

            val a by gen { Arb.int() }
            val b by gen { Arb.int() }
            val c by gen { Arb.int() }

            assume { a != b }
            assume { a != c }
            assume { b != c }

            a + b + c shouldBe c + b + a
         }
      }

      test("callbacks") {

         beforePermutation {
            // setup code
         }

         afterPermutation {
            // some tear down code
         }

         withPermutation {
            val a by gen { Arb.int() }
            val b by gen { Arb.int() }
            val c by gen { Arb.int() }

            a + b + c shouldBe c + b + a
         }
      }
   }
}

package com.sksamuel.kotest.engine.test.coroutines

import io.kotest.core.coroutines.backgroundScope
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineScopeFunSpecTest : FunSpec() {
   init {
      test("should advance time when using coroutine test scope").config(coroutineTestScope = true) {
         // if this isn't working, this test will stall
         backgroundScope.launch {
            delay(2.days)
         }

         launch {
            delay(2.days)
         }
      }
      context("container") {
         test("should advance time when using coroutine test scope in a nested test").config(coroutineTestScope = true) {
            // if this isn't working, this test will stall
            backgroundScope.launch {
               delay(2.days)
            }

            launch {
               delay(2.days)
            }
         }
      }
   }
}

class CoroutineScopeShouldSpecTest : ShouldSpec() {
   init {
      should("should advance time when using coroutine test scope").config(coroutineTestScope = true) {
         // if this isn't working, this test will stall
         backgroundScope.launch {
            delay(2.days)
         }

         launch {
            delay(2.days)
         }
      }
      context("container") {
         should("should advance time when using coroutine test scope in a nested test").config(coroutineTestScope = true) {
            // if this isn't working, this test will stall
            backgroundScope.launch {
               delay(2.days)
            }

            launch {
               delay(2.days)
            }
         }
      }
   }
}

class CoroutineScopeExpectSpecTest : ExpectSpec() {
   init {

      context("should advance time when using coroutine test scope in a context").config(coroutineTestScope = true) {
         // if this isn't working, this test will stall
         backgroundScope.launch {
            delay(2.days)
         }

         launch {
            delay(2.days)
         }
      }

      expect("should advance time when using coroutine test scope in an expect").config(coroutineTestScope = true) {
         // if this isn't working, this test will stall
         backgroundScope.launch {
            delay(2.days)
         }

         launch {
            delay(2.days)
         }
      }
   }
}

class CoroutineScopeFeatureSpecTest : FeatureSpec() {
   init {
      feature("should advance time when using coroutine test scope").config(coroutineTestScope = true) {
         // if this isn't working, this test will stall
         backgroundScope.launch {
            delay(2.days)
         }

         launch {
            delay(2.days)
         }
      }

      feature("container") {
         scenario("should advance time when using coroutine test scope in a nested scenario").config(coroutineTestScope = true) {
            // if this isn't working, this test will stall
            backgroundScope.launch {
               delay(2.days)
            }

            launch {
               delay(2.days)
            }
         }
      }
   }
}

class CoroutineScopeFreeSpecTest : FreeSpec() {
   init {
      "should advance time when using coroutine test scope".config(coroutineTestScope = true) {
         // if this isn't working, this test will stall
         backgroundScope.launch {
            delay(2.days)
         }

         launch {
            delay(2.days)
         }
      }
      "container" - {
         "should advance time when using coroutine test scope in a container".config(coroutineTestScope = true) {
            // if this isn't working, this test will stall
            backgroundScope.launch {
               delay(2.days)
            }

            launch {
               delay(2.days)
            }
         }
      }
   }
}

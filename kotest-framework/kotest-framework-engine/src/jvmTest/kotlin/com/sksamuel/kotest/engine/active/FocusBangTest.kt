package com.sksamuel.kotest.engine.active

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.time.TimeSource
import kotlin.time.measureTime

class FocusBangTest : FunSpec() {
   init {
      test("test case with f: prefix").config(coroutineTestScope = true) {
//         runTest {
            val elapsed = TimeSource.Monotonic.measureTime {
               val deferred = async {
                  delay(5_000) // will be skipped
               }
               deferred.await()
            }
            println(elapsed) // about 15ms on my machine
//         }
      }
   }
}

package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import kotlinx.coroutines.delay

@Isolate
class CallbackCoroutinesTest : FunSpec({

   var start: Long = 0
   var a: Long = 0
   var b: Long

   beforeTest {
      delay(250)
   }

   afterTest {
      delay(250)
   }

   beforeSpec {
      start = System.currentTimeMillis()
   }

   afterSpec {
      val end = System.currentTimeMillis()
      (end - start).shouldBeGreaterThan(250)
   }

   test("start the timer") {
      a = System.currentTimeMillis()
   }

   test("should be delayed a bit due to the callbacks between these tests") {
      b = System.currentTimeMillis()
      (b - a).shouldBeGreaterThan(500)
   }
})

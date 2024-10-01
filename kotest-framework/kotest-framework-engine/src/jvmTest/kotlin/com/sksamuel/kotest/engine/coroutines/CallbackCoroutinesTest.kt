package com.sksamuel.kotest.engine.coroutines

import io.kotest.common.testTimeSource
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark

@Isolate
@EnabledIf(LinuxCondition::class)
class CallbackCoroutinesTest : FunSpec({

   lateinit var start: TimeMark
   lateinit var a: TimeMark

   beforeTest {
      delay(250)
   }

   afterTest {
      delay(250)
   }

   beforeSpec {
      start = testTimeSource().markNow()
   }

   afterSpec {
      start.elapsedNow() shouldBeGreaterThan 250.milliseconds
   }

   test("start the timer") {
      a = testTimeSource().markNow()
   }

   test("should be delayed a bit due to the callbacks between these tests") {
      a.elapsedNow() shouldBeGreaterThan 500.milliseconds
   }
})

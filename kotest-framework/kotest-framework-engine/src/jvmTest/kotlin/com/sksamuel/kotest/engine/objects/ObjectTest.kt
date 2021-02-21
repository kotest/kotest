package com.sksamuel.kotest.engine.objects

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import java.util.concurrent.atomic.AtomicBoolean

val objectTests = AtomicBoolean(false)

@AutoScan
object ObjectTestProjectListener : AfterProjectListener {
   // if the object test wasn't picked up, then this will be false and fail
   override suspend fun afterProject() = objectTests.get().shouldBeTrue()
}

object ObjectTest : FunSpec() {
   init {
      test("objects should be detected") {
         objectTests.set(true)
      }
   }
}

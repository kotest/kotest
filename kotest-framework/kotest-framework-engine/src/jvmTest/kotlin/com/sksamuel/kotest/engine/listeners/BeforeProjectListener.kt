package com.sksamuel.kotest.engine.listeners

import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private var counter = 0

@AutoScan
class A : BeforeProjectListener {
   override suspend fun beforeProject() {
      counter++
   }
}

class BeforeProjectListenerTest : FunSpec({
   test("before project listener should be fired") {
      counter.shouldBe(1)
   }
})

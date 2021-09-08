package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestStatus
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class InvocationThreadErrorTest : FunSpec({

   test("invocation errors should be propagated") {
      val listener = CollectingTestEngineListener()
      TestEngineLauncher(listener)
         .withClasses(InvocationErrorsTests::class)
         .async()
      listener.tests.size shouldBe 2
      listener.tests.values.forAll { it.status shouldBe TestStatus.Error }

   }

})

private class InvocationErrorsTests : FunSpec({

   test("multiple invocations").config(invocations = 4) {
      error("boom")
   }

   test("multiple invocations on multiple threads").config(invocations = 4, threads = 3) {
      error("boom")
   }
})

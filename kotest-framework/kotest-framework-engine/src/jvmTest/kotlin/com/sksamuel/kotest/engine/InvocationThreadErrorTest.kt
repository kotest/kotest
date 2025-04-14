package com.sksamuel.kotest.engine

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationThreadErrorTest : FunSpec({

   test("invocation errors should be propagated") {
      val listener = CollectingTestEngineListener()
      TestEngineLauncher(listener)
         .withClasses(InvocationErrorsTests::class)
         .launch()
      listener.tests.keys.map { it.name.name } shouldBe setOf(
         "multiple invocations",
//         "multiple invocations on multiple threads"
      )
      listener.tests.values.forAll { it.isError shouldBe true }
   }
})

private class InvocationErrorsTests : FunSpec({

   test("multiple invocations").config(invocations = 4) {
      error("boom")
   }

//   xtest("multiple invocations on multiple threads").config(invocations = 4, threads = 3) {
//      error("boom")
//   }
})

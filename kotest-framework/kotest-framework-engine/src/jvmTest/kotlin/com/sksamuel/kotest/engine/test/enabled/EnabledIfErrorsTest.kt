package com.sksamuel.kotest.engine.test.enabled

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class EnabledIfErrorsTest : FunSpec() {
   init {
      test("error in enabledIf should disable test but run others") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(EnabledIfFailues::class)
            .launch()

         collector.result("a")!!.isErrorOrFailure shouldBe true
         collector.result("b")!!.isErrorOrFailure shouldBe true
         collector.result("c")!!.isSuccess shouldBe true
      }
   }
}

private class EnabledIfFailues : FunSpec({
   test("a").config(enabledIf = { error("foobar") }) {
   }
   test("b").config(enabledOrReasonIf = { error("foobar") }) {
   }
   test("c") {}
})

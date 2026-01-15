package com.sksamuel.kotest.engine.test.enabled

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class EnabledIfErrorsTest : FunSpec() {
   init {
      test("error in enabledIf should ignore test but run others") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(EnabledIfFailues::class)
            .execute()

         collector.result("a")!!.isIgnored shouldBe true
         collector.result("b")!!.isIgnored shouldBe true
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

package com.sksamuel.kotest.framework.engine

import io.kotest.common.KotestTesting
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

@OptIn(KotestTesting::class)
class AfterProjectListenerTest : FunSpec() {
   init {
      test("after project listeners defined in specs should be registered on all platforms") {

         TestEngineLauncher().withListener(NoopTestEngineListener)
            .withSpecRefs(SpecRef.Function({ MySpec() }, MySpec::class))
            .execute()

         executed shouldBe true
      }
   }
}

private var executed = false

class MySpec : FunSpec() {
   init {
      test("foo") {}
      afterProject { executed = true }
   }
}

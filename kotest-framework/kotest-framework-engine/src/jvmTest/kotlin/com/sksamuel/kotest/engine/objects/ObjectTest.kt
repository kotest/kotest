package com.sksamuel.kotest.engine.objects

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class ObjectSpecTest : FunSpec() {
   init {
      test("object specs should be supported") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MyObjectSpec::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         collector.result("foo")!!.isSuccess shouldBe true
      }
   }
}

private class MyObjectSpec : FunSpec({
   test("foo") {}
})

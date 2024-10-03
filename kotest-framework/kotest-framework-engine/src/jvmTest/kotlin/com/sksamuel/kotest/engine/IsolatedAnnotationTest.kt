package com.sksamuel.kotest.engine

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class IsolatedAnnotationTest : FunSpec() {
   init {
      test("classes annotated with @Isolate should run") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MyIsolatedSpec::class)
            .launch()
         collector.tests.shouldHaveSize(1)
         collector.tests.mapKeys { it.key.descriptor.id }[DescriptorId("a")]!!.isSuccess shouldBe true
      }
   }
}

@Isolate
private class MyIsolatedSpec : FunSpec() {
   init {
      test("a") {
      }
   }
}

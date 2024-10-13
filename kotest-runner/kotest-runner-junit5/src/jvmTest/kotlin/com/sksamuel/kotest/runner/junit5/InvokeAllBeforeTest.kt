package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

@EnabledIf(LinuxCondition::class)
class InvokeAllBeforeTest : FunSpec({
   test("should execute all beforeTest's blocks, even if we have some errors in it") {
      EngineTestKit
         .engine("kotest")
         .selectors(DiscoverySelectors.selectClass(ErrorInBeforeTest::class.java))
         .configurationParameter("allow_private", "true")
         .execute()

      ErrorInBeforeTest.count shouldBe 2 //because we failed on beforeTest - we didn't execute the 'some test'
   }

})

private class ErrorInBeforeTest : FreeSpec() {
   companion object {
      var count = 0
   }

   init {
      beforeTest { throw RuntimeException("First Error") }
      beforeTest { count += 1 }
      beforeAny { count += 1 }
      beforeAny { throw RuntimeException("Second Error") }
      "some case" { count += 1 }
   }
}

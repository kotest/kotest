package com.sksamuel.kotest.runner.junit5

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

class InvokeAllBeforeTest : FunSpec ({

   beforeSpec {
      System.setProperty(KotestEngineProperties.includePrivateClasses, "true")
   }

   afterSpec {
      System.setProperty(KotestEngineProperties.includePrivateClasses, "false")
   }

   test("should execute all beforeTest's blocks, even if we have some errors in it") {
      EngineTestKit
         .engine("kotest")
         .selectors(DiscoverySelectors.selectClass(ErrorInBeforeTest::class.java))
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

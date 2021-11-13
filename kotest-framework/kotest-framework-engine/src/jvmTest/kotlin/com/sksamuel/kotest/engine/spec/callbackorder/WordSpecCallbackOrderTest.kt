package com.sksamuel.kotest.engine.spec.callbackorder

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WordSpecCallbackOrderTest : WordSpec({

   var before = ""
   var after = ""
   var count = 0

   beforeTest {
      before += it.name.testName
   }

   afterTest { (test, _) ->
      count shouldBe 3
      after += test.name.testName
   }

   afterSpec {
      before shouldBe "w whens shouldt"
      after shouldBe "ts shouldw when"
   }

   "w" When {
      count++
      "s" should {
         count++
         "t" {
            count++
         }
      }
   }

})

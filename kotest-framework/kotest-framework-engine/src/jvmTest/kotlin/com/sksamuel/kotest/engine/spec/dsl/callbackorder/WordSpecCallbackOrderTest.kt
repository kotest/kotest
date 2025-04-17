package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WordSpecCallbackOrderTest : WordSpec({

   var before = ""
   var after = ""
   var count = 0

   beforeTest {
      before += it.name.name
   }

   afterTest { (test, _) ->
      count shouldBe 3
      after += test.name.name
   }

   afterSpec {
      before shouldBe "wst"
      after shouldBe "tsw"
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

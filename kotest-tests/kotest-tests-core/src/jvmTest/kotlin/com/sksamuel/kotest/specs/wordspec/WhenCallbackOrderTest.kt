package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WhenCallbackOrderTest : WordSpec({

   var before = ""
   var after = ""

   beforeTest {
      before += it.description.name.name
   }

   afterTest { (test, _) ->
      after += test.description.name.name
   }

   afterSpec {
      before shouldBe "w whens shouldt"
      after shouldBe "ts shouldw when"
   }

   "w" When {
      "s" should {
         "t" {
         }
      }
   }

})

package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WhenCallbackOrderTest : WordSpec({

   var before = ""
   var after = ""

   beforeTest {
      before += it.description.name.displayName()
   }

   afterTest { (test, _) ->
      after += test.description.name.displayName()
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

package com.sksamuel.kotest.focusbang

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.specs.WordSpec

class BangOverridenWordTest : WordSpec({
   "setting system property to override bang" should {
      var run = false
      System.setProperty("kotest.bang.disable", "true")
      "!allow this test to run" {
         run = true
      }
      System.getProperties().remove("kotest.bang.disable")
      run.shouldBeTrue()
   }
})

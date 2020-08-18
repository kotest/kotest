package com.sksamuel.kotest.engine.active

import io.kotest.core.internal.KotestEngineSystemProperties
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.booleans.shouldBeTrue

class BangPropertyOverrideTest : WordSpec({

   "setting system property to override bang" should {
      var run = false
      System.setProperty(KotestEngineSystemProperties.disableBangPrefix, "true")
      "!allow this test to run" {
         run = true
      }
      System.getProperties().remove(KotestEngineSystemProperties.disableBangPrefix)
      run.shouldBeTrue()
   }

})

package com.sksamuel.kotest.engine.active

import io.kotest.core.annotation.EnabledIf
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.booleans.shouldBeTrue

@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class BangPropertyOverrideTest : WordSpec({

   "setting system property to override bang" should {
      var run = false
      withSystemProperty(KotestEngineProperties.DISABLE_BANG_PREFIX, "true") {
         "!allow this test to run" {
            run = true
         }
      }
      run.shouldBeTrue()
   }

})

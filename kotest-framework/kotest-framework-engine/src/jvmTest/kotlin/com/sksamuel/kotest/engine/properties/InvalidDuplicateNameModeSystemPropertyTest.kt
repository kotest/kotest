package com.sksamuel.kotest.engine.properties

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.result.shouldBeFailure

class InvalidDuplicateNameModeSystemPropertyTest : FunSpec() {
   init {
      test("invalid duplicate name mode value should error") {
         withSystemProperty(KotestEngineProperties.duplicateTestNameMode, "qwerty") {
            runCatching { SpecConfigResolver().duplicateTestNameMode(this@InvalidDuplicateNameModeSystemPropertyTest) }.shouldBeFailure()
         }
      }
   }
}

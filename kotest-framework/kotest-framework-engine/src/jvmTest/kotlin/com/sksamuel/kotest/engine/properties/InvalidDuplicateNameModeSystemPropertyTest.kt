package com.sksamuel.kotest.engine.properties

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.ConfigManager
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.result.shouldBeFailure

class InvalidDuplicateNameModeSystemPropertyTest : FunSpec() {
   init {
      test("invalid duplicate name mode value should error") {
         withSystemProperty(KotestEngineProperties.duplicateTestNameMode, "qwerty") {
            ConfigManager.compile(ProjectConfiguration()) { emptyList() }.shouldBeFailure()
         }
      }
   }
}

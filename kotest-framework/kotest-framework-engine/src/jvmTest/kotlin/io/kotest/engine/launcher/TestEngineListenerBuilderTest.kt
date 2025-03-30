package io.kotest.engine.launcher

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.matchers.types.shouldBeInstanceOf

@EnabledIf(NotMacOnGithubCondition::class)
class TestEngineListenerBuilderTest : FunSpec() {
   init {

      test("specifying teamcity should return TeamCityTestEngineListener") {
         TestEngineListenerBuilder
            .builder()
            .withType(TestEngineListenerBuilder.TEAMCITY)
            .build()
            .shouldBeInstanceOf<TeamCityTestEngineListener>()
      }

      test("specifying enchanced should return EnhancedConsoleTestEngineListener") {
         TestEngineListenerBuilder
            .builder()
            .withType(TestEngineListenerBuilder.ENHANCED)
            .build()
            .shouldBeInstanceOf<EnhancedConsoleTestEngineListener>()
      }
   }
}

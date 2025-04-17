package io.kotest.engine.launcher

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.ConsoleTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.matchers.types.shouldBeInstanceOf

@EnabledIf(LinuxOnlyGithubCondition::class)
class TestEngineListenerBuilderTest : FunSpec() {
   init {

      test("specifying teamcity should return TeamCityTestEngineListener") {
         TestEngineListenerBuilder
            .builder()
            .withType(TestEngineListenerBuilder.LISTENER_TC)
            .build()
            .shouldBeInstanceOf<TeamCityTestEngineListener>()
      }

      test("specifying enchanced should return EnhancedConsoleTestEngineListener") {
         TestEngineListenerBuilder
            .builder()
            .withType(TestEngineListenerBuilder.LISTENER_CONSOLE)
            .build()
            .shouldBeInstanceOf<ConsoleTestEngineListener>()
      }
   }
}

package io.kotest.engine.launcher

import io.kotest.engine.listener.ConsoleTestEngineListener
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener

data class TestEngineListenerBuilder(
   private val type: String?,
) {

   companion object {

      const val TEAMCITY = "teamcity"
      const val ENHANCED = "enhanced"
      const val DEFAULT = "default"

      fun builder(): TestEngineListenerBuilder = TestEngineListenerBuilder(null)
   }

   fun withType(type: String?): TestEngineListenerBuilder = copy(type = type)

   fun build(): TestEngineListener {
      return when (type) {
         TEAMCITY -> TeamCityTestEngineListener()
         ENHANCED -> EnhancedConsoleTestEngineListener()
         else if isIntellij() -> TeamCityTestEngineListener()
         else -> ConsoleTestEngineListener()
      }
   }

   // this system property is added by intellij itself when running tasks
   private fun isIntellij() = System.getProperty("idea.active") != null
}

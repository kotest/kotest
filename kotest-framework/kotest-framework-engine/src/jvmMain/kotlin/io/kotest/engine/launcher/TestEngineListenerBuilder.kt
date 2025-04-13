package io.kotest.engine.launcher

import io.kotest.engine.listener.ConsoleTestEngineListener
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener

/**
 * Builds a [TestEngineListener] based on the type which is suitable
 * for test engines launched externally, by gradle or from intellij for example.
 */
data class TestEngineListenerBuilder(
   private val type: String?,
) {

   companion object {

      // the value used to specify the team city format
      const val LISTENER_TC = "teamcity"

      // the value used to specify a console format
      const val LISTENER_CONSOLE = "enhanced"

      internal const val IDEA_PROP = "idea.active"

      fun builder(): TestEngineListenerBuilder = TestEngineListenerBuilder(null)
   }

   fun withType(type: String?): TestEngineListenerBuilder = copy(type = type)

   fun build(): TestEngineListener {
      return when (type) {
         LISTENER_TC -> TeamCityTestEngineListener()
         LISTENER_CONSOLE -> EnhancedConsoleTestEngineListener()
         // if not speciifed, we'll try to detect instead
         else if isIntellij() -> TeamCityTestEngineListener()
         else -> ConsoleTestEngineListener()
      }
   }

   // this system property is added by intellij itself when running tasks
   private fun isIntellij() = System.getProperty(IDEA_PROP) != null
}

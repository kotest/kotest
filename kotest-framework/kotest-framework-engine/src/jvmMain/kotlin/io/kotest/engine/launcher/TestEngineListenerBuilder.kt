package io.kotest.engine.launcher

import io.kotest.engine.listener.ConsoleTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener

/**
 * Builds a [TestEngineListener] based on the type which is suitable
 * for test engines launched externally, by Gradle or from intellij, for example.
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

      // set only by the IntelliJ plugin's non-Gradle (e.g. Maven) run configuration launcher,
      // so that IntelliJ can render a real nested test tree when it is parsing these TeamCity
      // messages directly from a forked JVM, without changing the flattened format relied on
      // by other TeamCity consumers (JS/Native etc).
      internal const val NEST_CONTAINERS_PROP = "kotest.engine.listener.teamcity.nestContainers"

      fun builder(): TestEngineListenerBuilder = TestEngineListenerBuilder(null)
   }

   fun withType(type: String?): TestEngineListenerBuilder = copy(type = type)

   fun build(): TestEngineListener {
      return when (type) {
         LISTENER_TC -> TeamCityTestEngineListener(nestContainers = nestContainers())
         LISTENER_CONSOLE -> ConsoleTestEngineListener()
         // if not speciifed, we'll try to detect instead
         else if isIntellij() -> TeamCityTestEngineListener(nestContainers = nestContainers())
         else -> ConsoleTestEngineListener()
      }
   }

   // this system property is added by intellij itself when running tasks
   private fun isIntellij() = System.getProperty(IDEA_PROP) != null

   private fun nestContainers() = System.getProperty(NEST_CONTAINERS_PROP) == "true"
}

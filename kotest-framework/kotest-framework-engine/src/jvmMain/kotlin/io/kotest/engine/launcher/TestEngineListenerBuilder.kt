package io.kotest.engine.launcher

import com.github.ajalt.mordant.TermColors
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener

/**
 * Builds a [TestEngineListener] based on the type and termcolors specified which is suitable
 * for test engines launched externally, by gradle or from intellij for example.
 */
data class TestEngineListenerBuilder(
   private val type: String?,
   private val termcolors: String?,
) {

   companion object {

      // the value used to specify the team city format
      const val LISTENER_TC = "teamcity"

      // the value used to specify a console format
      const val LISTENER_CONSOLE = "enhanced"

      const val COLORS_PLAIN = "ansi16"
      const val COLORS_TRUE = "true"

      internal const val IDEA_PROP = "idea.active"

      fun builder(): TestEngineListenerBuilder = TestEngineListenerBuilder(null, null)
   }

   fun withType(type: String?): TestEngineListenerBuilder = copy(type = type)
   fun withTermColors(colors: String?): TestEngineListenerBuilder = copy(termcolors = colors)

   fun build(): TestEngineListener {
      return when (type) {
         LISTENER_TC -> TeamCityTestEngineListener()
         LISTENER_CONSOLE -> EnhancedConsoleTestEngineListener(colours())
         // if not speciifed, we'll try to detect instead
         else if isIntellij() -> TeamCityTestEngineListener()
         else -> EnhancedConsoleTestEngineListener(colours())
      }
   }

   // this system property is added by intellij itself when running tasks
   private fun isIntellij() = System.getProperty(IDEA_PROP) != null

   internal fun colours(): TermColors {
      return when (termcolors) {
         COLORS_TRUE -> TermColors(TermColors.Level.TRUECOLOR)
         COLORS_PLAIN -> TermColors(TermColors.Level.ANSI16)
         else -> TermColors()
      }
   }
}

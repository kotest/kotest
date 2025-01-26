package io.kotest.engine.launcher

import com.github.ajalt.mordant.TermColors
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener

data class TestEngineListenerBuilder(
   private val type: String?,
   private val termcolors: String?,
) {

   companion object {

      const val TEAMCITY = "teamcity"
      const val ENHANCED = "enhanced"
      const val DEFAULT = "default"
      const val TRUECOLOR = "true"
      const val ANSI256 = "ansi256"
      const val ANSI16 = "ansi16"

      fun builder(): TestEngineListenerBuilder = TestEngineListenerBuilder(null, null)
   }

   fun withType(type: String?): TestEngineListenerBuilder = copy(type = type)
   fun withTermColors(colors: String?): TestEngineListenerBuilder = copy(termcolors = colors)

   fun build(): TestEngineListener {
      return when (type) {
         TEAMCITY -> TeamCityTestEngineListener()
         ENHANCED -> EnhancedConsoleTestEngineListener(TermColors())
         else if isIntellij() -> TeamCityTestEngineListener()
         else -> EnhancedConsoleTestEngineListener(colours())
      }
   }

   private fun isIntellij() = System.getProperty("idea.active") != null

   internal fun colours(): TermColors {
      return when (termcolors) {
         TRUECOLOR -> TermColors(TermColors.Level.TRUECOLOR)
         ANSI256 -> TermColors(TermColors.Level.ANSI256)
         ANSI16 -> TermColors(TermColors.Level.ANSI16)
         else -> TermColors()
      }
   }
}

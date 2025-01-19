package io.kotest.engine.launcher

import com.github.ajalt.mordant.TermColors
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener

data class ConsoleTestEngineListenerBuilder(
   private val type: String?,
   private val termcolors: String?,
) {

   companion object {
      fun builder(): ConsoleTestEngineListenerBuilder = ConsoleTestEngineListenerBuilder(null, null)
   }

   fun withType(type: String?): ConsoleTestEngineListenerBuilder = copy(type = type)
   fun withTermColors(colors: String?): ConsoleTestEngineListenerBuilder = copy(termcolors = colors)

   fun build(): TestEngineListener {
      return when (type) {
         "teamcity" -> TeamCityTestEngineListener()
         "enhanced" -> EnhancedConsoleTestEngineListener(TermColors())
         else if isIntellij() -> TeamCityTestEngineListener()
         else -> EnhancedConsoleTestEngineListener(colours())
      }
   }

   private fun isIntellij() = System.getProperty("idea.active") != null

   internal fun colours(): TermColors {
      return when (termcolors) {
         "true" -> TermColors(TermColors.Level.TRUECOLOR)
         "ansi256" -> TermColors(TermColors.Level.ANSI256)
         "ansi16" -> TermColors(TermColors.Level.ANSI16)
         else -> TermColors()
      }
   }
}

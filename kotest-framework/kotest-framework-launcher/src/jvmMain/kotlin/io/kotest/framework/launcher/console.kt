package io.kotest.framework.launcher

import com.github.ajalt.mordant.TermColors
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener

///**
// * Creates a [TestEngineListener] that will write to the console, using the provided
// * [args] to determine which output format to use.
// */
//internal fun createConsoleListener(args: LauncherArgs): TestEngineListener {
//   return try {
//      // we support "teamcity", "taycan", "enhanced" as special values
//      // taycan was the name for the fancy kotest output but has been renamed to simply enhanced
//      when (args.listener) {
//         "teamcity" -> TeamCityTestEngineListener()
//         "taycan", "enhanced" -> EnhancedConsoleTestEngineListener(colours(args))
//         null -> defaultConsoleListener()
//         else -> Class.forName(args.listener).getDeclaredConstructor().newInstance() as TestEngineListener
//      }
//
//   } catch (t: Throwable) {
//      println(t.message)
//      t.printStackTrace()
//      defaultConsoleListener()
//   }
//}
//
//internal fun colours(args: LauncherArgs): TermColors {
//   return when (args.termcolor) {
//      "true" -> TermColors(TermColors.Level.TRUECOLOR)
//      "ansi256" -> TermColors(TermColors.Level.ANSI256)
//      "ansi16" -> TermColors(TermColors.Level.ANSI16)
//      "auto" -> TermColors()
//      else -> TermColors()
//   }
//}
//
//// returns a TestEngineListener appropriate for the environment when none was specified
//// If we are running from intellij, we use an IDEA compatible team city writer
//// otherwise we use the default enhanced writer
//internal fun defaultConsoleListener(): TestEngineListener =
//   if (System.getProperty("idea.active") != null)
//      TeamCityTestEngineListener()
//   else
//      EnhancedConsoleTestEngineListener(TermColors())

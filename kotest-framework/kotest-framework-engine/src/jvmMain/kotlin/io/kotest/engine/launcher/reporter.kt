package io.kotest.engine.launcher

import com.github.ajalt.mordant.TermColors
import io.kotest.core.execution.ExecutionContext
import io.kotest.engine.reporter.ConsoleReporter
import io.kotest.engine.reporter.Reporter
import io.kotest.engine.reporter.TaycanConsoleReporter
import io.kotest.engine.reporter.TeamCityConsoleReporter

internal fun createReporter(args: LauncherArgs, context: ExecutionContext): Reporter {
   return try {

      // we support "teamcity" and "taycan" as special values
      val reporter = when (args.reporter) {
         "teamcity" -> TeamCityConsoleReporter()
         "taycan" -> TaycanConsoleReporter()
         else -> Class.forName(args.reporter).getDeclaredConstructor().newInstance() as Reporter
      }

      if (reporter is ConsoleReporter) {
         val term = when (args.termcolor) {
            "true" -> TermColors(TermColors.Level.TRUECOLOR)
            "ansi256" -> TermColors(TermColors.Level.ANSI256)
            "ansi16" -> TermColors(TermColors.Level.ANSI16)
            "auto" -> TermColors()
            else -> TermColors()
         }
         reporter.setTerm(term)
      }
      reporter

   } catch (t: Throwable) {
      println(t.message)
      t.printStackTrace()
      defaultReporter(context)
   }
}

// returns a console writer appropriate for the environment when none was specified
// attempts to find the idea_rt.jar and if it exists, we assume we are running from intellij, and thus
// change our output to be an IDEA compatible team city writer
// otherwise we use the default taycan writer
internal fun defaultReporter(context: ExecutionContext): Reporter = try {
   Class.forName("com.intellij.rt.execution.CommandLineWrapper")
   TeamCityConsoleReporter()
} catch (_: ClassNotFoundException) {
   TaycanConsoleReporter()
}

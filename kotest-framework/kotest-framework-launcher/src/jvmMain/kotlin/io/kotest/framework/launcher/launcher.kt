package io.kotest.framework.launcher

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.TermColors
import io.kotest.core.NamedTag
import io.kotest.core.Tags
import io.kotest.framework.console.BasicConsoleWriter
import io.kotest.framework.console.ConsoleWriter
import io.kotest.framework.console.MochaConsoleWriter
import io.kotest.framework.console.TaycanReporter
import io.kotest.framework.console.TeamCityConsoleWriter
import kotlin.system.exitProcess

// this main method will launch the KotestEngine
fun main(args: Array<String>) = Execute().main(args)

class Execute : CliktCommand(name = "Kotest Launcher") {

   private val test by option("--testpath", help = "A path to the test to execute. Nested tests will also be executed")

   private val includeTags by option("--include-tags", help = "Which tags to be included").multiple()
   private val excludeTags by option("--exclude-tags", help = "Which tags to be excluded").multiple()
   private val tagexpression by option("--tags", help = "Tag expression to control which tests are executed")

   private val source by option("--source", help = "Optional string describing how the launcher was invoked")

   private val packageName by option(
      "--package",
      help = "Setting this option restricts tests to the package or subpackages"
   )

   private val spec by option(
      "--spec",
      help = "Specify the fully qualified name of the spec class which contains the test to execute"
   )

   private val termcolor by option(
      "--termcolor",
      help = "Specify true to force true colour on the terminal; auto to have autodefault"
   )

   private val slowDuration by option(
      "--slow-duration",
      help = "Optional time in millis controlling when a test is marked as slow"
   ).int().default(1000)

   private val verySlowDuration by option(
      "--very-slow-duration",
      help = "Optional time in millis controlling when a test is marked as very slow"
   ).int().default(3000)

   private val writerClass by option(
      "--writer",
      help = "Specify the name of console writer implementation."
   )

   override fun run() {

      val term = when (termcolor) {
         "true" -> TermColors(TermColors.Level.TRUECOLOR)
         "ansi256" -> TermColors(TermColors.Level.ANSI256)
         else -> TermColors()
      }

      val tags = when {
         tagexpression != null -> Tags(tagexpression ?: "")
         includeTags.isEmpty() && excludeTags.isEmpty() -> Tags.Empty
         else -> Tags(
            included = includeTags.map { NamedTag(it) }.toSet(),
            excluded = includeTags.map { NamedTag(it) }.toSet()
         )
      }

      val writer = when (writerClass) {
         "mocha" -> MochaConsoleWriter(term, slowDuration, verySlowDuration)
         "basic" -> BasicConsoleWriter()
         "teamcity" -> TeamCityConsoleWriter()
         "taycan" -> TaycanReporter(term)
         else -> defaultWriter()
      }

      execute(writer, packageName, spec, test, tags)

      // there could be threads in the background that will stop the launcher shutting down
      // for example if a test keeps a thread running
      // so we must force the exit
      if (writer.hasErrors()) exitProcess(-1) else exitProcess(0)
   }
}

// returns a console writer appropriate for the environment when none was specified
// attempts to find the idea_rt.jar and if it exists, we assume we are running from intellij, and thus
// change our output to be an IDEA compatible team city writer
// otherwise we use the default colour writer
fun defaultWriter(): ConsoleWriter = try {
   Class.forName("com.intellij.rt.execution.CommandLineWrapper")
   TeamCityConsoleWriter()
} catch (_: ClassNotFoundException) {
   BasicConsoleWriter()
}

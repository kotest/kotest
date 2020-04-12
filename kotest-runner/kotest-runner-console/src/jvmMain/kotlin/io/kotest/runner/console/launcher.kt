package io.kotest.runner.console

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.TermColors
import io.kotest.core.StringTag
import io.kotest.core.Tags
import kotlin.system.exitProcess

// this main method will launch the KotestConsoleRunner
fun main(args: Array<String>) = Execute().main(args)

class Execute : CliktCommand(name = "Kotest console runner") {

   private val test by option("--testpath", help = "A path to the test to execute. Nested tests will also be executed")

   private val includeTags by option("--include-tags", help = "Which tags to be included").multiple()
   private val excludeTags by option("--exclude-tags", help = "Which tags to be excluded").multiple()

   private val source by option("--source", help = "Optional string describing how the launcher was invoked")

   private val packageName by option(
      "--package",
      help = "Setting this option restricts tests to the package or subpackages"
   )

   private val spec by option(
      "--spec",
      help = "Specifies the spec where the tests are located. If omitted then all tests run"
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

      val term = if (source == "kotest-gradle-plugin") TermColors(TermColors.Level.ANSI256) else TermColors()

      val tags = if (includeTags.isEmpty() && excludeTags.isEmpty()) null else Tags(
         included = includeTags.map { StringTag(it) }.toSet(),
         excluded = includeTags.map { StringTag(it) }.toSet()
      )

      val writer = when (writerClass) {
         "mocha" -> MochaConsoleWriter(term, slowDuration, verySlowDuration)
         "basic" -> BasicConsoleWriter()
         else -> defaultWriter()
      }

      val runner = KotestConsoleRunner(writer)
      runner.execute(packageName, spec, test, tags)

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

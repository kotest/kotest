package io.kotlintest.runner.console

import com.github.ajalt.mordant.TermColors
import net.sourceforge.argparse4j.ArgumentParsers

// this main method will launch the KotlinTestConsoleRunner
fun main(args: Array<String>) {

  val parser = ArgumentParsers.newFor("kotlintest").build().defaultHelp(true).description("KotlinTest console runner")
  parser.addArgument("--test").help("Specify the test name to execute (can be a leaf test or a container test)")
  parser.addArgument("--spec").help("Specify the fully qualified name of the spec class which contains the test to execute")
  parser.addArgument("--writer").help("Specify the name of console writer implementation. Defaults TeamCity")
  parser.addArgument("--source").help("Optional string describing how the launcher was invoked")
  val ns = parser.parseArgs(args)

  val writerClass: String? = ns.getString("writer")
  val spec: String? = ns.getString("spec")
  val test: String? = ns.getString("test")
  val source: String? = ns.getString("source")

  val term = if (source == "kotlintest-gradle-plugin") TermColors(TermColors.Level.ANSI256) else TermColors()

  val writer = when (writerClass) {
    "mocha" -> MochaConsoleWriter(term)
    "basic" -> BasicConsoleWriter()
    else -> TeamCityConsoleWriter()
  }

  val runner = KotlinTestConsoleRunner(writer)
  runner.execute(spec, test)

  // there could be threads in the background that will stop the launcher shutting down
  // for example if a test keeps a thread running
  // so we must force the exit
  if (writer.hasErrors()) System.exit(-1) else System.exit(0)
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
package io.kotlintest.runner.console

import net.sourceforge.argparse4j.ArgumentParsers
import kotlin.reflect.full.createInstance

// this main method will launch the KotlinTestConsoleRunner
fun main(args: Array<String>) {

  val parser = ArgumentParsers.newFor("kotlintest").build().defaultHelp(true).description("KotlinTest console runner")
  parser.addArgument("--test").help("Specify the test name to execute (can be a leaf test or a container test)")
  parser.addArgument("--spec").help("Specify the fully qualified name of the spec class which contains the test to execute")
  parser.addArgument("--writer").help("Specify the fully qualified name of the console writer implementation. Defaults to io.kotlintest.runner.console.TeamCityConsoleWriter")
  val ns = parser.parseArgs(args)

  val writerClass: String? = ns.getString("writer")
  val spec: String? = ns.getString("spec")
  val test: String? = ns.getString("test")

  val writer = if (writerClass == null) defaultWriter() else Class.forName(writerClass).kotlin.createInstance() as ConsoleWriter
  val runner = KotlinTestConsoleRunner(writer)
  runner.execute(spec, test)

  if (writer.hasErrors()) System.exit(-1)
}

// returns a console writer appropriate for the environment when none was specified
// attempts to find the idea_rt.jar and if it exists, we assume we are running from intellij, and thus
// change our output to be an IDEA compatible team city writer
// otherwise we use the default colour writer
fun defaultWriter(): ConsoleWriter = try {
  Class.forName("com.intellij.rt.execution.CommandLineWrapper")
  TeamCityConsoleWriter()
} catch (_: ClassNotFoundException) {
  DefaultConsoleWriter()
}
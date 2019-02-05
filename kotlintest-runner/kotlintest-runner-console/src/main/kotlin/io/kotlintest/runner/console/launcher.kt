package io.kotlintest.runner.console

import net.sourceforge.argparse4j.ArgumentParsers

// this main method will launch the KotlinTestConsoleRunner
fun main(args: Array<String>) {

  println("KotlinTest Console Runner [args=$args]")

  val parser = ArgumentParsers.newFor("kotlintest").build().defaultHelp(true).description("KotlinTest console runner")
  parser.addArgument("--test").help("Specify the test name to execute (can be a leaf test or a container test)")
  parser.addArgument("--spec").help("Specify the fully qualified name of the spec class which contains the test to execute")
  val ns = parser.parseArgs(args)

  val test = ns.getString("test")
  val spec = ns.getString("spec")

  val runner = KotlinTestConsoleRunner()
  runner.execute(spec, test)
}
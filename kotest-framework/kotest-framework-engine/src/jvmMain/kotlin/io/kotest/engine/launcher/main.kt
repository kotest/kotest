package io.kotest.engine.launcher

import io.kotest.core.spec.Spec
import io.kotest.engine.cli.parseArgs
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.LoggingTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.runBlocking
import kotlin.reflect.KClass
import kotlin.system.exitProcess

/**
 * The entry point for the launcher.
 *
 * Parses the cli args, creates the listeners and creates a test launcher using a [TestEngineLauncherBuilder].
 *
 * --- IMPORTANT NOTE ---
 * This is used by the Gradle and Intellij plugins (and other third party clients).
 * Therefore, the package name and contract for this main method *MUST* remain backwards compatible.
 */
fun main(args: Array<String>) {

   val launcherArgs = parseArgs(args.toList())
   val specsArg = launcherArgs["specs"] ?: error("The --specs arg must be provided")

   // what classes to run? We must be launched with a list.
   // That list comes from the --specs flag
   @Suppress("UNCHECKED_CAST")
   val classes = specsArg.split(';').map { Class.forName(it).kotlin as KClass<out Spec> }

   // we can filter to a test or parent test, eg from the command line or from the intellij plugin
   // this filter comes from the --filter flag
   val filter = launcherArgs["filter"]

   val console = TestEngineListenerBuilder.builder()
      .withType(launcherArgs["listener"])
      .withTermColors(launcherArgs["termcolors"])
      .build()

   // we want to collect the results, so we can check if we need exit with an error
   val collector = CollectingTestEngineListener()

   val launcher = TestEngineLauncherBuilder.builder()
      .withClasses(classes)
      .addListener(LoggingTestEngineListener) // we use this to write to the kotest log file
      .addListener(collector)
      .addListener(ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(console))).build()

   runBlocking {
      launcher.async()
   }

   // there could be threads in the background that will stop the launcher shutting down
   // for example if a test keeps a thread running,
   // so we must force the exit
   if (collector.errors) exitProcess(-1) else exitProcess(0)
}

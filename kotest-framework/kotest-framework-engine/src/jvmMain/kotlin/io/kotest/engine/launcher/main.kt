package io.kotest.engine.launcher

import com.github.ajalt.mordant.TermColors
import io.kotest.core.spec.Spec
import io.kotest.engine.cli.parseArgs
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener
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

   val collector = CollectingTestEngineListener()

   // what tests to run? We must be launched with a list.
   // That list comes from the --specs flag
   val classes = launcherArgs["specs"]!!.split(';').map { Class.forName(it) as KClass<out Spec> }

   val launcher = TestEngineLauncherBuilder.builder()
      .withClasses(classes)
      .addListener(LoggingTestEngineListener) // we use this to write to the kotest log file
      .addListener(collector) // we want to collect the results so we can check if we need exit with an error
      .addListener(
         ThreadSafeTestEngineListener(
            PinnedSpecTestEngineListener(
               EnhancedConsoleTestEngineListener(
                  TermColors(TermColors.Level.ANSI16)
               )
            )
         )
      ).build()

   runBlocking {
      launcher.async()
   }

   // there could be threads in the background that will stop the launcher shutting down
   // for example if a test keeps a thread running,
   // so we must force the exit
   if (collector.errors) exitProcess(-1) else exitProcess(0)
}

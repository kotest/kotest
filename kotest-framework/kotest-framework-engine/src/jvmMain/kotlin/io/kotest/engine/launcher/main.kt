package io.kotest.engine.launcher

import com.github.ajalt.mordant.TermColors
import io.kotest.engine.cli.parseArgs
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener
import io.kotest.engine.listener.LoggingTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.runBlocking
import kotlin.system.exitProcess

/**
 * The entry point for the launcher.
 *
 * Parses the cli args, creates the listeners and creates a test launcher using [setupLauncher].
 *
 * --- IMPORTANT NOTE ---
 * This is used by the Gradle and Intellij plugins (and other third party clients).
 * Therefore, the package name and contract for this main method *MUST* remain backwards compatible.
 */
fun main(args: Array<String>) {

   val launcherArgs = parseArgs(args.toList())

   val collector = CollectingTestEngineListener()

   // what tests to run? We must be launched with a list.
   // That list comes from the --tests flag
   val classes = launcherArgs["tests"].map { ch ->  }

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

///**
// * The entry point for the launcher.
// *
// * Parses the cli args, creates the listeners and creates a test launcher using [setupLauncher].
// *
// * This is used by the kotest-intellij-plugin (and other third party clients).
// * Therefore, the package name and args for this main method should remain backwards compatible.
// */
//@KotestInternal
//fun main(args: Array<String>) {
//
//   val launcherArgs = parseLauncherArgs(args.toList())
//
//   val collector = CollectingTestEngineListener()
//   val listener = CompositeTestEngineListener(
//      listOf(
//         collector,
//         LoggingTestEngineListener,
//         ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(createConsoleListener(launcherArgs))),
//      )
//   )
//
//   runBlocking {
//      setupLauncher(launcherArgs, listener).fold(
//         { it.async() },
//         {
//            // if we couldn't create the launcher we'll display those errors
//            listener.engineStarted()
//            listener.engineFinished(listOf(it))
//         },
//      )
//   }
//
//   // there could be threads in the background that will stop the launcher shutting down
//   // for example if a test keeps a thread running,
//   // so we must force the exit
//   if (collector.errors) exitProcess(-1) else exitProcess(0)
//}

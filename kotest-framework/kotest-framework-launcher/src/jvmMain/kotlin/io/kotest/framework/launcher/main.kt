package io.kotest.framework.launcher

import com.github.ajalt.mordant.TermColors
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.EnhancedConsoleTestEngineListener
import io.kotest.engine.listener.LoggingTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.runBlocking
import io.kotest.framework.discovery.DiscoveryBuilder
import io.kotest.framework.discovery.DiscoveryRequestBuilder
import io.kotest.framework.discovery.DiscoveryResult
import kotlin.system.exitProcess

object Args {
   const val TESTS = "tests"
}

/**
 * The entry point for the launcher.
 *
 * Parses the cli args, creates the listeners and creates a test launcher using [setupLauncher].
 *
 * This is used by the kotest-intellij-plugin (and other third party clients).
 * Therefore, the package name and args for this main method should remain backwards compatible.
 */
fun main(args: Array<String>) {

   val launcherArgs = parseArgs(args.toList())
   val specs = specs()
   println("Found specs: ${specs.specs.size}")

   val collector = CollectingTestEngineListener()

   val launcher = TestEngineLauncherBuilder.builder()
      .addListener(LoggingTestEngineListener) // we use this to write to the log file
      .addListener(collector)  // we want to collect the results so we can check if we need exit with an error
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

private fun specs(): DiscoveryResult {
   println("Starting discovery")
   val discovery = DiscoveryBuilder.builder()
      .addDefaultBlacklistPackages()
      .withJarScanning(false)
      .withNestedJarScanning(false)
      .withExternalClasses(true)
      .build()
   val request = DiscoveryRequestBuilder.builder().build()
   return discovery.discover(request)
}

package io.kotest.engine.launcher

import io.kotest.common.KotestInternal
import io.kotest.common.runBlocking
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.LoggingTestEngineListener
import io.kotest.engine.listener.Node
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import kotlin.system.exitProcess
import kotlin.time.Duration

/**
 * The entry point for the launcher.
 *
 * Parses the cli args, creates the reporter and passes them to the [execute] method.
 *
 * This is used by the kotest-intellij-plugin (and other third party clients).
 * Therefore, the package name and args for this main method should remain backwards compatible.
 */
@KotestInternal
fun main(args: Array<String>) {

   val launcherArgs = parseLauncherArgs(args.toList())

   val collector = CollectingTestEngineListener()
   val listener = CompositeTestEngineListener(
      listOf(
         collector,
         LoggingTestEngineListener,
         ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(createConsoleListener(launcherArgs))),
      )
   )

   runBlocking {
      setupLauncher(launcherArgs, listener).fold(
         { it.async() },
         {
            // if we couldn't create the launcher we'll display those errors
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.Error(Duration.ZERO, it))
         },
      )
   }

   // there could be threads in the background that will stop the launcher shutting down
   // for example if a test keeps a thread running,
   // so we must force the exit
   if (collector.errors) exitProcess(-1) else exitProcess(0)
}

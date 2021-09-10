package io.kotest.engine.launcher

import io.kotest.core.Tags
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.engine.reporter.IsolatedReporter
import kotlin.system.exitProcess

/**
 * The entry point for the launcher.
 *
 * Parses the cli args, creates the reporter and passes them to the [execute] method.
 *
 * This is used by the kotest-intellij-plugin (and probably other third party clients).
 * Therefore, the args for this main method should remain backwards compatible.
 */
fun main(args: Array<String>) {

   val launcherArgs = parseLauncherArgs(args.toList())
   val tags = Tags(launcherArgs.tagExpression)

   val reporter = IsolatedReporter(createReporter(launcherArgs))
   execute(
      reporter,
      launcherArgs.packageName,
      launcherArgs.spec,
      launcherArgs.testpath,
      tags,
      (launcherArgs.dumpconfig ?: System.getProperty(KotestEngineProperties.dumpConfig)) == "true"
   )

   // there could be threads in the background that will stop the launcher shutting down
   // for example if a test keeps a thread running,
   // so we must force the exit
   if (reporter.hasErrors()) exitProcess(-1) else exitProcess(0)
}

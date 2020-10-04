package io.kotest.engine.launcher

import io.kotest.core.Tags
import kotlin.system.exitProcess

fun main(args: Array<String>) {

   val launcherArgs = parseLauncherArgs(args.toList())
   val tags = Tags(launcherArgs.tagExpression)

   val reporter = createReporter(launcherArgs)
   execute(reporter, launcherArgs.packageName, launcherArgs.spec, launcherArgs.testpath, tags)

   // there could be threads in the background that will stop the launcher shutting down
   // for example if a test keeps a thread running
   // so we must force the exit
   if (reporter.hasErrors()) exitProcess(-1) else exitProcess(0)
}

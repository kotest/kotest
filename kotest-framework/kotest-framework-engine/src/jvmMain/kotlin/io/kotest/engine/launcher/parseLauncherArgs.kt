package io.kotest.engine.launcher

import io.kotest.engine.cli.parseArgs

data class LauncherArgs(
   // A path to the test to execute. Nested tests will also be executed
   val testpath: String?,
   // restricts tests to the package or subpackages
   val packageName: String?,
   // the fully qualified name of the spec class which contains the test to execute
   val spec: String?,
   // true to force true colour on the terminal; auto to have autodefault
   val termcolor: String?,
   // the fully qualified name of a reporter implementation
   val reporter: String?,
   // Tag expression to control which tests are executed
   val tagExpression: String?,
   // true to output the configuration values when the Engine is created
   val dumpconfig: Boolean?
)

fun parseLauncherArgs(args: List<String>): LauncherArgs {
   val a = parseArgs(args)
   return LauncherArgs(
      testpath = a["testpath"],
      packageName = a["package"],
      spec = a["spec"],
      termcolor = a["termcolor"],
      // writer is for backwards compatibility
      reporter = a["reporter"] ?: a["writer"],
      tagExpression = a["tags"],
      dumpconfig = a["dumpconfig"]?.toBoolean(),
   )
}

package io.kotest.framework.gradle

import org.gradle.process.JavaExecSpec
import org.gradle.process.internal.JavaExecAction

/**
 * This [TestLauncherArgsJavaExecConfiguration] is responsible for configuring the args on a [JavaExecAction]
 * that will run tests through the kotest engine by executing the [io.kotest.engine.launcher.MainKt] main class.
 */
internal data class TestLauncherArgsJavaExecConfiguration(
   private val tags: String? = null,
   private val include: String? = null,
   private val targetName: String? = null,
   private val moduleTestReportsDir: String? = null,
   private val rootTestReportsDir: String? = null,
   private val specs: List<String> = emptyList(),
) {

   companion object {

      // used to specify if we want team city service messages or console output
      private const val ARG_LISTENER = "--listener"

      private const val ARG_TAGS = "--tags"

      // required to pass the non-filtered list of specs to the engine
      private const val ARG_SPECS = "--specs"

      // used to filter to a single spec or test within a spec
      private const val ARG_INCLUDE = "--include"

      private const val ARG_TARGET_NAME = "--target-name"

      // used to filter to a single spec or test within a spec
      private const val ARG_MODULE_TEST_REPORTS_DIR = "--module-test-reports-dir"
      private const val ARG_ROOT_TEST_REPORTS_DIR = "--root-test-reports-dir"

      // the value used to specify the team city format
      private const val LISTENER_TC = "teamcity"

      // the value used to specify a console format
      private const val LISTENER_CONSOLE = "enhanced"

      // note: this package cannot change as it is part of the public api
      internal const val LAUNCHER_MAIN_CLASS = "io.kotest.engine.launcher.MainKt"
   }

   fun withCommandLineTags(tags: String?): TestLauncherArgsJavaExecConfiguration {
      return copy(tags = tags)
   }

   fun withSpecs(specs: List<String>): TestLauncherArgsJavaExecConfiguration {
      return copy(specs = specs)
   }

   fun withInclude(include: String?): TestLauncherArgsJavaExecConfiguration {
      return copy(include = include)
   }

   fun withTargetName(targetName: String?): TestLauncherArgsJavaExecConfiguration {
      return copy(targetName = targetName)
   }

   fun withModuleTestReportsDir(dir: String): TestLauncherArgsJavaExecConfiguration {
      return copy(moduleTestReportsDir = dir)
   }

   fun withRootTestReportsDir(dir: String): TestLauncherArgsJavaExecConfiguration {
      return copy(rootTestReportsDir = dir)
   }

   fun configure(exec: JavaExecSpec) {
      exec.args(args())
   }

   /**
    * Returns the args to send to the launcher
    */
   private fun args(): List<String> =
      listenerArgs() + tagsArg() + specsArg() + includeArg() + testReportsDirArg() + targetNameArg()

   /**
    * If we are running inside intellij, we assume the user has the intellij Kotest plugin installed,
    * and so we will use the teamcity format, which the plugin will parse and use to render an SMTest view.
    * If they don't, the output will be the raw service-message format which is designed for parsing
    * not human consumption.
    *
    * If we are not running from intellij, then we use a console output format.
    */
   private fun listenerArgs(): List<String> {
      return when {
         IntellijUtils.isIntellij() -> listOf(ARG_LISTENER, LISTENER_TC)
         else -> listOf(ARG_LISTENER, LISTENER_CONSOLE)
      }
   }

   /**
    * Returns an arg to filter down to a single spec or test within a spec.
    * This is used for example when running a single test from the kotest intellij plugin.
    */
   private fun includeArg(): List<String> {
      return if (include == null) emptyList() else listOf(ARG_INCLUDE, include)
   }

   /**
    * Returns an arg to filter down to a single spec or test within a spec.
    * This is used for example when running a single test from the kotest intellij plugin.
    */
   private fun testReportsDirArg(): List<String> {
      return (if (moduleTestReportsDir != null) listOf(
         ARG_MODULE_TEST_REPORTS_DIR,
         moduleTestReportsDir
      ) else emptyList()) +
         (if (rootTestReportsDir != null) listOf(ARG_ROOT_TEST_REPORTS_DIR, rootTestReportsDir) else emptyList())
   }

   private fun targetNameArg(): List<String> {
      return if (targetName == null) emptyList()
      else listOf(ARG_TARGET_NAME, targetName)
   }

   /**
    * Returns args to be used for the tag expression.
    *
    * If --tags was passed as a command line arg, then that takes precedence over the value
    * set in the gradle build.
    *
    * Returns empty list if no tag expression was specified.
    */
   private fun tagsArg(): List<String> {
      tags?.let { return listOf(ARG_TAGS, it) }
//      project.kotest()?.tags?.orNull?.let { return listOf(TagsArg, it) }
      return emptyList()
   }

   /**
    * Returns an arg to specify the spec classes.
    * This is a semi-colon separated list of fully qualified class names.
    *
    * If the --specs arg was passed as a command line arg, then we use that as is, otherwise
    * the gradle plugin will have scanned the runtime classpath and found all the spec classes.
    */
   private fun specsArg(): List<String> {
      require(specs.isNotEmpty()) { "Specs must be provided by the gradle plugin" }
      return listOf(ARG_SPECS, specs.joinToString(";"))
   }
}

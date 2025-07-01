package io.kotest.framework.gradle

import org.gradle.process.ExecSpec
import org.gradle.process.internal.ExecAction

/**
 * This [NativeExecConfiguration] is responsible for configuring an [ExecAction] that will run tests
 * through the kotest engine by executing the debugTest executable created by the native compiler.
 */
internal data class NativeExecConfiguration(
   private val executable: String,
   private val tags: String? = null,
   private val descriptor: String? = null,
   private val specs: List<String> = emptyList(),
) {

   companion object {

      private const val ARG_TAGS = "--tags"

      // the value used to specify the team city format
      private const val LISTENER_TC = "teamcity"

      // the value used to specify a console format
      private const val LISTENER_CONSOLE = "enhanced"

      internal const val IDEA_PROP = "idea.active"
   }

   fun withCommandLineTags(tags: String?): NativeExecConfiguration {
      return copy(tags = tags)
   }

   fun withDescriptor(descriptor: String?): NativeExecConfiguration {
      return copy(descriptor = descriptor)
   }

   fun configure(exec: ExecSpec) {
      exec.setCommandLine(executable)
      exec.environment("kotest.framework.runtime.native.listener", "TeamCity") // todo support non TCSM
      if (descriptor != null)
         exec.environment("kotest.framework.runtime.native.descriptor", descriptor)

      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      exec.isIgnoreExitValue = true
   }

   /**
    * If we are running inside intellij, we assume the user has the intellij Kotest plugin installed,
    * and so we will use the teamcity format, which the plugin will parse and use to render an SMTest view.
    * If they don't, the output will be the raw service-message format which is designed for parsing
    * not human consumption.
    *
    * If we are not running from intellij, then we use a console output format.
    */
   private fun listenerType(): String {
      return when {
         isIntellij() -> LISTENER_TC
         else -> LISTENER_CONSOLE
      }
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
    * We use the idea system property to determine if we are running inside intellij.
    */
   private fun isIntellij() = System.getProperty(IDEA_PROP) != null
}

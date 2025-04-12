package io.kotest.framework.gradle

import org.gradle.api.file.FileCollection
import org.gradle.process.JavaExecSpec
import org.gradle.process.internal.JavaExecAction

/**
 * This [TestLauncherExecBuilder] is responsible for configuring a [JavaExecAction] that will run tests
 * through the kotest engine.
 */
internal data class TestLauncherExecBuilder(
   private val classpath: FileCollection? = null,
   private val tags: String? = null,
   private val descriptor: String? = null,
   private val candidates: List<String> = emptyList(),
) {

   companion object {

      // used to specify if we want team city or console output
      private const val ARG_LISTENER = "--listener"

      // used to specify the color of the output
      private const val ARG_TERMCOLOR = "--termcolor"

      private const val ARG_TAGS = "--tags"

      // required to pass the candidates to the engine
      private const val ARG_CANDIDATES = "--candidates"

      // used to filter to a single spec or test within a spec
      private const val ARG_DESCRIPTOR = "--descriptor"

      // the value used to specify the team city format
      private const val LISTENER_TC = "teamcity"

      // the value used to specify a console format
      private const val LISTENER_CONSOLE = "enhanced"

      private const val COLORS_PLAIN = "ansi16"
      private const val COLORS_TRUE = "true"

      // note: this package cannot change as it is part of the public api
      internal const val LAUNCHER_MAIN_CLASS = "io.kotest.engine.launcher.MainKt"

      internal const val IDEA_PROP = "idea.active"
   }

   fun withCommandLineTags(tags: String?): TestLauncherExecBuilder {
      return copy(tags = tags)
   }

   fun withClasspath(classpath: FileCollection): TestLauncherExecBuilder {
      return copy(classpath = classpath)
   }

   fun withCandidates(candidates: List<String>): TestLauncherExecBuilder {
      return copy(candidates = candidates)
   }

   fun withDescriptor(descriptor: String?): TestLauncherExecBuilder {
      return copy(descriptor = descriptor)
   }

   fun configure(spec: JavaExecSpec) {
      spec.mainClass.set(LAUNCHER_MAIN_CLASS)
      spec.classpath(this@TestLauncherExecBuilder.classpath)
      spec.args(this@TestLauncherExecBuilder.args())

      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      spec.isIgnoreExitValue = true
   }

   /**
    * Returns the args to send to the launcher
    */
   private fun args() = listenerArgs() + tagsArg() + candidatesArg() + descriptorArg()

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
         isIntellij() -> listOf(ARG_LISTENER, LISTENER_TC, ARG_TERMCOLOR, COLORS_PLAIN)
         else -> listOf(ARG_LISTENER, LISTENER_CONSOLE, ARG_TERMCOLOR, COLORS_TRUE)
      }
   }

   /**
    * Returns an arg to filter down to a single spec or test within a spec.
    * This is used for example when running a single test from the kotest intellij plugin.
    */
   private fun descriptorArg(): List<String> {
      if (descriptor == null) return emptyList()
      return listOf(ARG_DESCRIPTOR, descriptor)
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
    * Returns an arg to specify the candidate classes.
    * This is a semi-colon separated list of fully qualified class names.
    *
    * If the --candidates arg was passed as a command line arg, then we use that as is, otherwise
    * the gradle plugin will have scanned the runtime classpath and found all the spec classes.
    */
   private fun candidatesArg(): List<String> {
      return if (candidates.isEmpty()) emptyList() else listOf(ARG_CANDIDATES, candidates.joinToString(";"))
   }

   /**
    * We use the idea system property to determine if we are running inside intellij.
    */
   private fun isIntellij() = System.getProperty(IDEA_PROP) != null
}

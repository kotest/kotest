package io.kotest.framework.gradle

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.concurrent.ExecutorFactory
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.JavaExecAction
import java.io.OutputStream

/**
 * This [TestLauncherExecBuilder] is responsible for creating a [JavaExecAction] that will run tests
 * through the kotest engine.
 */
data class TestLauncherExecBuilder(
   private val fileResolver: FileResolver,
   private val fileCollectionFactory: FileCollectionFactory,
   private val executorFactory: ExecutorFactory,
   private val classpath: FileCollection?,
   private val tags: String?,
   private val consumer: OutputStream?,
) {

   companion object {
      private const val LISTENER_ARG = "--listener"
      private const val TERMCOLOR_ARG = "--termcolor"
      private const val TAGS_ARG = "--tags"
      private const val TC_LISTENER = "teamcity"
      private const val ENHANCED_CONSOLE_LISTENER = "enhanced"
      private const val PLAIN_COLOURS = "ansi16"
      private const val TRUE_COLOURS = "ansi256"

      const val LAUNCHER_MAIN_CLASS = "io.kotest.engine.launcher.MainKt"
      const val IDEA_PROP = "idea.active"

      fun builder(
         fileResolver: FileResolver,
         fileCollectionFactory: FileCollectionFactory,
         executorFactory: ExecutorFactory,
      ): TestLauncherExecBuilder {
         return TestLauncherExecBuilder(
            fileResolver = fileResolver,
            fileCollectionFactory = fileCollectionFactory,
            executorFactory = executorFactory,
            tags = null,
            classpath = null,
            consumer = null,
         )
      }
   }

   fun withCommandLineTags(tags: String?): TestLauncherExecBuilder {
      return copy(tags = tags)
   }

   fun withClasspath(classpath: FileCollection): TestLauncherExecBuilder {
      return copy(classpath = classpath)
   }

   fun withStandardOutputConsumer(consumer: OutputStream): TestLauncherExecBuilder {
      return copy(consumer = consumer)
   }

   /**
    * Returns a [JavaExecAction] configured to execute the test engine launcher.
    */
   fun build(): JavaExecAction {
      val exec = DefaultExecActionFactory.of(
         /* fileResolver = */ fileResolver,
         /* fileCollectionFactory = */ fileCollectionFactory,
         /* executorFactory = */ executorFactory,
         /* temporaryFileProvider = */ null
      ).newJavaExecAction()
//      copyTo(exec)

      exec.mainClass.set(LAUNCHER_MAIN_CLASS)
      exec.classpath = classpath
//      exec.jvmArgs = allJvmArgs
      exec.args = args()
//      if (consumer != null) exec.standardOutput = consumer

      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      exec.isIgnoreExitValue = true

      return exec
   }

   /**
    * Returns the args to send to the launcher.
    *
    * If we are running inside intellij, we assume the user has the intellij Kotest plugin installed,
    * and we use the teamcity format, otherwise a standard console format is used.
    *
    * If they don't, the output will be the raw service-message format which is designed for parsing
    * not human consumption.
    */
   private fun args() = when {
      isIntellij() -> listOf(
         LISTENER_ARG,
         TC_LISTENER,
         TERMCOLOR_ARG,
         PLAIN_COLOURS
      ) + tagArgs()

      else -> listOf(
         LISTENER_ARG,
         ENHANCED_CONSOLE_LISTENER,
         TERMCOLOR_ARG,
         TRUE_COLOURS
      ) + tagArgs()
   }

   /**
    * We use the idea system property to determine if we are running inside intellij.
    */
   private fun isIntellij() = System.getProperty(IDEA_PROP) != null

   /**
    * Returns args to be used for the tag expression.
    *
    * If --tags was passed as a command line arg, then that takes precedence over the value
    * set in the gradle build.
    *
    * Returns empty list if no tag expression was specified.
    */
   private fun tagArgs(): List<String> {
      tags?.let { return listOf(TAGS_ARG, it) }
//      project.kotest()?.tags?.orNull?.let { return listOf(TagsArg, it) }
      return emptyList()
   }
}

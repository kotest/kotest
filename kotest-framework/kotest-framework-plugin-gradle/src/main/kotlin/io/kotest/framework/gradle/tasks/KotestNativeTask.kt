package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.NativeExecConfiguration
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestNativeTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   @get:Input
   abstract val exe: Property<String>

   @TaskAction
   protected fun execute() {
      testReportsDir.get().asFile.mkdirs()
      val result = executors.exec {
         NativeExecConfiguration(exe.get())
            .withDescriptor(include.orNull)
            .withCommandLineTags(tags.orNull)
            .withTestReportsDir(testReportsDir.get().asFile.absolutePath)
            .configure(this)
      }

      if (result.exitValue != 0) {
         println("There were test failures, exit code: ${result.exitValue}")
         result.rethrowFailure()
      }
   }
}

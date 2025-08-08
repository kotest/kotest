package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.NativeExecConfiguration
import org.gradle.api.GradleException
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

   @get:Input
   abstract val targetName: Property<String>

   @TaskAction
   protected fun execute() {
      moduleTestReportsDir.get().asFile.mkdirs()
      rootTestReportsDir.get().asFile.mkdirs()
      val result = executors.exec {
         NativeExecConfiguration(exe.get())
            .withDescriptor(include.orNull)
            .withCommandLineTags(tags.orNull)
            .withRootTestReportsDir(rootTestReportsDir.get().asFile.absolutePath)
            .withModuleTestReportsDir(moduleTestReportsDir.get().asFile.absolutePath)
            .withTargetName(targetName.get())
            .configure(this)
      }

      if (result.exitValue != 0) {
         throw GradleException("Test suite failed with errors")
      }
   }
}

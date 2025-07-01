package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.NativeExecConfiguration
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestNativeTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   @get:Input
   abstract val target: Property<KotlinTargetWithTests<*, *>>

   @TaskAction
   protected fun execute() {

      val binaryPath = "bin/${target.get().name}/debugTest/test.kexe"
      val kexe = project.layout.buildDirectory.get().asFile.resolve(binaryPath).absolutePath

      val result = executors.exec {
         NativeExecConfiguration(kexe)
            .withDescriptor(descriptor.orNull)
            .withCommandLineTags(tags.orNull)
            .configure(this)
         println(this.environment)
      }

      if (result.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }
}

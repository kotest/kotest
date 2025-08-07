package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.SpecsResolver
import io.kotest.framework.gradle.TestLauncherJavaExecConfiguration
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestJvmTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   // this is the sourceset that contains the tests
   // this will be usally "test" for JVM projects and "jvmTest" for multiplatform projects
   @get:Input
   abstract val testSourceSetClasspath: Property<FileCollection>

   @TaskAction
   protected fun execute() {

      val specs = SpecsResolver.specs(specs, packages, testSourceSetClasspath.get())
      moduleTestReportsDir.get().asFile.mkdirs()
      rootTestReportsDir.get().asFile.mkdirs()

      if (specs.isEmpty())
         return // if there are no specs, we do not run the task

      val result = executors.javaexec {
         TestLauncherJavaExecConfiguration()
            .withClasspath(testSourceSetClasspath.get())
            .withSpecs(specs)
            .withDescriptor(include.orNull)
            .withModuleTestReportsDir(moduleTestReportsDir.get().asFile.absolutePath)
            .withRootTestReportsDir(rootTestReportsDir.get().asFile.absolutePath)
            .withCommandLineTags(tags.orNull)
            .configure(this)
      }

      if (result.exitValue != 0) {
         throw GradleException("Test suite failed with errors")
      }
   }

}

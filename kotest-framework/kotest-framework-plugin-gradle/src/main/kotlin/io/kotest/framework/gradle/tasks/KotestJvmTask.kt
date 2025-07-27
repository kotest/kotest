package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.SpecsResolver
import io.kotest.framework.gradle.TestLauncherJavaExecConfiguration
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestJvmTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   // this is the name of the sourceset that contains the tests
   // this will be usally "test" for JVM projects and "jvmTest" for multiplatform projects
   @get:Input
   abstract val sourceSetName: Property<String>

   @TaskAction
   protected fun execute() {

      val java = project.extensions.getByType(JavaPluginExtension::class.java)
      val test = java.sourceSets.findByName(sourceSetName.get())
         ?: throw StopExecutionException("Could not find source set '${sourceSetName.get()}'")

      val specs = SpecsResolver.specs(specs, packages, test.runtimeClasspath)

      val result = executors.javaexec {
         TestLauncherJavaExecConfiguration()
            .withClasspath(test.runtimeClasspath)
            .withSpecs(specs)
            .withDescriptor(descriptor.orNull)
            .withCommandLineTags(tags.orNull)
            .configure(this)
      }

      if (result.exitValue != 0) {
         println("Test execution failed with exit code ${result.exitValue}")
         result.rethrowFailure()
      }
   }
}

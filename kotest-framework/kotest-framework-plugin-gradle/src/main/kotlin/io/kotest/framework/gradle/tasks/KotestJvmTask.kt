package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.SpecsResolver
import io.kotest.framework.gradle.TestLauncherJavaExecConfiguration
import org.gradle.api.GradleException
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestJvmTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   @TaskAction
   protected fun execute() {

      // todo better way to detect the test compilations ?
      val java = project.extensions.getByType(JavaPluginExtension::class.java)
      val test = java.sourceSets.findByName("test") ?: return

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
         throw GradleException("There were test failures")
      }
   }
}

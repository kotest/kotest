package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.SpecsResolver
import io.kotest.framework.gradle.TestLauncherJavaExecConfiguration
import org.gradle.api.GradleException
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestAndroidTask @Inject internal constructor(
   private val executors: ExecOperations,
   private val objects: ObjectFactory,
) : AbstractKotestTask() {

   @get:Input
   abstract val compilationName: Property<String>

   @TaskAction
   protected fun execute() {

      val ext = project.extensions.getByType(KotlinAndroidExtension::class.java)
      val target = ext.target
      if (target is KotlinAndroidTarget) {

         // example compilations for a typical project:
         // [debug, debugAndroidTest, debugUnitTest, release, releaseUnitTest]

         ext.target.compilations
            .matching { it.name == compilationName.get() }
            .forEach {
               executeCompilation(it)
            }

      }
   }

   private fun executeCompilation(compilation: KotlinCompilation<*>) {

      // this is all the transitive dependencies declared in the module
      val rt = compilation.allAssociatedCompilations
         .mapNotNull { it.runtimeDependencyConfigurationName }
         .mapNotNull { project.configurations.findByName(it) }

      // this contains our tests and resources
      val testOutputs = compilation.output.allOutputs

      val classpathWithTests = objects.fileCollection()
         .from(rt)
         .from(testOutputs)

      val specs = SpecsResolver.specs(specs, packages, classpathWithTests)

      val result = executors.javaexec {
         TestLauncherJavaExecConfiguration()
            .withClasspath(classpathWithTests)
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

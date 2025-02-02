package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.KotestExtension
import io.kotest.framework.gradle.TestLauncherExecBuilder
import org.gradle.api.GradleException
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestAndroidTask @Inject internal constructor(
   private val executors: ExecOperations,
   private val objects: ObjectFactory,
) : AbstractKotestTask() {

   @get:Input
   abstract val compilationNames: ListProperty<String>

   @TaskAction
   protected fun execute() {
      val ext = project.extensions.getByType(KotlinAndroidExtension::class.java)
      ext.target.compilations
         .matching { it.name.endsWith("UnitTest") }
         .matching { compilationNames.get().contains(it.name) }
         .forEach {
            executeCompilation(it)
         }
   }

   private fun executeCompilation(compilation: KotlinCompilation<*>) {

      // TODO do not use Project during task execution
      val ext = project.extensions.getByType(KotestExtension::class.java)

      // todo how do we get a handle to this location without hard coding the path ?
      val testClassesDir = "${ext.androidTestSource}/${compilation.compilationName}"
      val testClassesPath = project.layout.buildDirectory.get().asFile.resolve(testClassesDir)

      // todo how do we get a handle to this location without hard coding the path ?
      val classesDir = "${ext.androidTestSource}/${compilation.compilationName.removeSuffix("UnitTest")}"
      val classesPath = project.layout.buildDirectory.get().asFile.resolve(classesDir)

      val runtimeName = compilation.runtimeDependencyConfigurationName ?: error("No runtimeDependencyConfigurationName")
      val runtimeClasspath = project.configurations[runtimeName]

      val classpathWithTests = objects.fileCollection()
         .from(runtimeClasspath)
         .from(classesPath)
         .from(testClassesPath)

      val candidates = candidates(classpathWithTests)

      val exec = TestLauncherExecBuilder()
         .withClasspath(classpathWithTests)
         .withCandidates(candidates)
         .withDescriptor(descriptor.orNull)
         .withCommandLineTags(tags.orNull)

      val result = executors.javaexec {
         exec.configure(this)
      }

      if (result.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }

}

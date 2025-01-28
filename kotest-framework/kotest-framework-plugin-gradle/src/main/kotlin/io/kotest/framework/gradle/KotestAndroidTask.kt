package io.kotest.framework.gradle

import org.gradle.api.GradleException
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.concurrent.ExecutorFactory
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
open class KotestAndroidTask @Inject constructor(
   private val fileResolver: FileResolver,
   private val fileCollectionFactory: FileCollectionFactory,
   private val executorFactory: ExecutorFactory,
) : AbstractKotestTask() {

   @Input
   val compilationNames = project.objects.listProperty(String::class.java)

   @TaskAction
   fun execute() {
      val ext = project.extensions.getByType(KotlinAndroidExtension::class.java)
      ext.target.compilations
         .matching { it.name.endsWith("UnitTest") }
         .matching { compilationNames.get().contains(it.name) }
         .forEach {
            executeCompilation(it)
         }
   }

   private fun executeCompilation(compilation: KotlinCompilation<*>) {

      val ext = project.extensions.getByType(KotestExtension::class.java)

      // todo how do we get a handle to this location without hard coding the path ?
      val testClassesDir = "${ext.androidTestSource}/${compilation.compilationName}"
      val testClassesPath = project.layout.buildDirectory.get().asFile.toPath().resolve(testClassesDir)

      // todo how do we get a handle to this location without hard coding the path ?
      val classesDir = "${ext.androidTestSource}/${compilation.compilationName.removeSuffix("UnitTest")}"
      val classesPath = project.layout.buildDirectory.get().asFile.toPath().resolve(classesDir)

      val runtimeName = compilation.runtimeDependencyConfigurationName ?: error("No runtimeDependencyConfigurationName")
      val runtimeClasspath = project.configurations[runtimeName]

      val classpathWithTests = runtimeClasspath.plus(
         fileCollectionFactory.fixed(
            listOf(classesPath.toFile(), testClassesPath.toFile())
         )
      )

      val candidates = candidates(classpathWithTests)

      val exec = TestLauncherExecBuilder
         .builder(fileResolver, fileCollectionFactory, executorFactory)
         .withClasspath(classpathWithTests)
         .withCandidates(candidates)
         .withDescriptor(descriptor())
         .withCommandLineTags(tags())
         .build()

      val result = exec.execute()

      if (result.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }

}

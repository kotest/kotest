package io.kotest.framework.gradle

import org.gradle.api.GradleException
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.CacheableTask
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

   @TaskAction
   fun execute() {
      val android = project.extensions.findByType(KotlinAndroidExtension::class.java)
      android?.target?.compilations?.forEach {
         // todo better way to detect the test compilations ?
         if (it.name.endsWith("UnitTest"))
            executeCompilation(it)
      }
   }

   private fun executeCompilation(compilation: KotlinCompilation<*>) {

      // todo how do we get a handle to this location without hard coding the path ?
      val classesFolder = "tmp/kotlin-classes/${compilation.compilationName}"
      val classesPath = project.layout.buildDirectory.get().asFile.toPath().resolve(classesFolder)
      val runtimeName = compilation.runtimeDependencyConfigurationName ?: error("No runtimeDependencyConfigurationName")
      val runtimeClasspath = project.configurations[runtimeName]
      val classpathWithTests = runtimeClasspath.plus(fileCollectionFactory.fixed(classesPath.toFile()))

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

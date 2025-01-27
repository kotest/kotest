package io.kotest.framework.gradle

import org.gradle.api.GradleException
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.concurrent.ExecutorFactory
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
open class KotestJvmTask @Inject constructor(
   private val fileResolver: FileResolver,
   private val fileCollectionFactory: FileCollectionFactory,
   private val executorFactory: ExecutorFactory,
) : AbstractKotestTask() {

   @TaskAction
   fun execute() {

      // todo better way to detect the test compilations ?
      val java = project.extensions.getByType(JavaPluginExtension::class.java)
      val test = java.sourceSets.findByName("test") ?: return

      val candidates = this@KotestJvmTask.candidates(test.runtimeClasspath)
      candidates.forEach { println("spec: $it") }

      val exec = TestLauncherExecBuilder
         .builder(fileResolver, fileCollectionFactory, executorFactory)
         .withClasspath(test.runtimeClasspath)
         .withCandidates(candidates)
         .withDescriptor(descriptor())
         .withCommandLineTags(tags())
         .build()
      val result = exec.execute()

      if (result?.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }
}

package io.kotest.framework.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.internal.concurrent.ExecutorFactory
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
open class KotestTask @Inject constructor(
   private val fileResolver: FileResolver,
   private val fileCollectionFactory: FileCollectionFactory,
   private val executorFactory: ExecutorFactory,
) : DefaultTask() {

   private var tags: String? = null
   private var tests: String? = null

   // gradle will call this if --tests was specified on the command line
   @Suppress("unused")
   @Option(option = "tests", description = "Filter to a single spec and/or test")
   fun setTests(tests: String) {
      this.tests = tests
   }

   // gradle will call this if --tags was specified on the command line
   @Suppress("unused")
   @Option(option = "tags", description = "Set tag expression to include or exclude tests")
   fun setTags(tags: String) {
      this.tags = tags
   }

   @TaskAction
   fun executeTests() {
      println("Running tests with tags $tags and tests $tests")
      //val testResultsDir = project.buildDir.resolve("test-results")
      val testSourceSet = project.javaTestSourceSet() ?: return
      println("sourceset $testSourceSet")

      val specs = TestClassDetector().detect(testSourceSet.runtimeClasspath.asFileTree)
      specs.forEach { println("detected spec: $it") }

      val result = try {
         val builder = TestLauncherExecBuilder
            .builder(fileResolver, fileCollectionFactory, executorFactory)
            .withClasspath(testSourceSet.runtimeClasspath)
            .withSpecs(specs)
            .withCommandLineTags(tags)
         val exec = builder.build()
         exec.execute()
      } catch (e: Exception) {
         println(e)
         e.printStackTrace()
         throw GradleException("Test process failed", e)
      }

      if (result.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }
}

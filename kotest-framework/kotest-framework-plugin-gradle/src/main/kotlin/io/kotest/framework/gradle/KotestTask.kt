package io.kotest.framework.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.plugins.JavaPluginExtension
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
   private var specs: String? = null
   private var packages: String? = null

   companion object {
      const val DELIMITER = ";"
   }

   // gradle will call this if --tests was specified on the command line
   @Suppress("unused")
   @Option(option = "tests", description = "Filter to a single spec and/or test")
   fun setTests(tests: String) {
      this.tests = tests
   }

   // gradle will call this if --specs was specified on the command line
   @Option(option = "specs", description = "The input set of specs if we want to specify instead of scanning")
   fun setSpecs(specs: String) {
      this.specs = specs
   }

   // gradle will call this if --packages was specified on the command line
   @Option(option = "packages", description = "Specify the packages to scan for tests")
   fun setPackages(packages: String) {
      this.packages = packages
   }

   // gradle will call this if --tags was specified on the command line
   @Option(option = "tags", description = "Set tag expression to include or exclude tests")
   fun setTags(tags: String) {
      this.tags = tags
   }

   @TaskAction
   fun executeTests() {
      val sourceSets = project.extensions.findByType(JavaPluginExtension::class.java)?.sourceSets?.findByName("test") ?: return
      println("sourceSets $sourceSets")

      val specs = specs(sourceSets.runtimeClasspath)
      specs.forEach { println("spec: $it")  }

      val result = try {
         val builder = TestLauncherExecBuilder
            .builder(fileResolver, fileCollectionFactory, executorFactory)
            .withClasspath(sourceSets.runtimeClasspath)
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

   /**
    * Returns the specs to run based on the command line options and detection from the classpath.
    */
   private fun specs(candidates: FileCollection): List<String> {
      // if the --specs option was specified, then that is the highest priority and we take
      // that as a delimited list of fully qualified class names
      val specsFromOptions = specs?.split(DELIMITER)
      if (specsFromOptions != null) return specsFromOptions

      // If specs was omitted, then we scan the classpath
      val specsFromScanning = TestClassDetector().detect(candidates.asFileTree)

      // if packages was set, we filter down to only classes in those packages
      val packagesFromOptions = packages?.split(DELIMITER)?.toSet()
      val filteredSpecs = if (packagesFromOptions == null) specsFromScanning else specsFromScanning.filter { spec ->
         packagesFromOptions.contains(spec.packageName)
      }
      return filteredSpecs.map { it.qualifiedName }
   }
}

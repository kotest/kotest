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
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
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
   @Suppress("unused")
   @Option(option = "specs", description = "The input set of specs if we want to specify instead of scanning")
   fun setSpecs(specs: String) {
      this.specs = specs
   }

   // gradle will call this if --packages was specified on the command line
   @Suppress("unused")
   @Option(option = "packages", description = "Specify the packages to scan for tests")
   fun setPackages(packages: String) {
      this.packages = packages
   }

   // gradle will call this if --tags was specified on the command line
   @Suppress("unused")
   @Option(option = "tags", description = "Set tag expression to include or exclude tests")
   fun setTags(tags: String) {
      this.tags = tags
   }

   @TaskAction
   fun executeTests() {
      try {

         val android = project.extensions.findByType(KotlinAndroidExtension::class.java)
         android?.target?.compilations?.forEach {
            // todo better way to detect the test compilations ?
            if (it.name.endsWith("UnitTest"))
               executeAndroid(it)
         }

         val java = project.extensions.findByType(JavaPluginExtension::class.java)
         if (java != null)
            executeJvm(java)

      } catch (e: Exception) {
         println(e)
         e.printStackTrace()
         throw GradleException("Test process failed", e)
      }
   }

   private fun executeJvm(java: JavaPluginExtension) {

      // todo better way to detect the test compilations ?
      val test = java.sourceSets.findByName("test") ?: return

      val specs = specs(test.runtimeClasspath)
      specs.forEach { println("spec: $it") }

      val builder = TestLauncherExecBuilder
         .builder(fileResolver, fileCollectionFactory, executorFactory)
         .withClasspath(test.runtimeClasspath)
         .withSpecs(specs)
         .withCommandLineTags(tags)
      val exec = builder.build()
      val result = exec.execute()

      if (result?.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }

   private fun executeAndroid(compilation: KotlinCompilation<*>) {

      // todo how do we get a handle to this location without hard coding the path ?
      val classesFolder = "tmp/kotlin-classes/${compilation.compilationName}"
      val classesPath = project.layout.buildDirectory.get().asFile.toPath().resolve(classesFolder)
      val runtimeName = compilation.runtimeDependencyConfigurationName ?: error("No runtimeDependencyConfigurationName")
      val runtimeClasspath = project.configurations[runtimeName]
      val classpathWithTests = runtimeClasspath.plus(fileCollectionFactory.fixed(classesPath.toFile()))

      val specs = specs(classpathWithTests)
      specs.forEach { println("spec: $it") }

      val builder = TestLauncherExecBuilder
         .builder(fileResolver, fileCollectionFactory, executorFactory)
         .withClasspath(classpathWithTests)
         .withSpecs(specs)
         .withCommandLineTags(tags)
      val exec = builder.build()
      val result = exec.execute()

      if (result.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }

   private fun KotlinProjectExtension.allKotlinCompilations(): Collection<KotlinCompilation<*>> =
      when (this) {
         is KotlinMultiplatformExtension -> targets.flatMap { it.compilations }
         is KotlinSingleTargetExtension<*> -> target.compilations
         else -> error("Unsupported KotlinProjectExtension type: $this")
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
      println("specsFromScanning: $specsFromScanning")

      // if packages was set, we filter down to only classes in those packages
      val packagesFromOptions = packages?.split(DELIMITER)?.toSet()
      val filteredSpecs = if (packagesFromOptions == null) specsFromScanning else specsFromScanning.filter { spec ->
         packagesFromOptions.contains(spec.packageName)
      }
      return filteredSpecs.map { it.qualifiedName }
   }
}

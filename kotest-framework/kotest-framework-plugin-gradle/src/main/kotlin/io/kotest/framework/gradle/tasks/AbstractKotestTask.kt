package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.TestClassDetector
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

abstract class AbstractKotestTask internal constructor() : DefaultTask() {

   @get:Option(option = "descriptor", description = "Filter to a single spec or test")
   @get:Input
   @get:Optional
   abstract val descriptor: Property<String>

   @get:Option(option = "tests", description = "Filter to a test path expression")
   @get:Input
   @get:Optional
   abstract val tests: Property<String>

   @get:Option(option = "candidates", description = "The candidates list to avoid scanning")
   @get:Input
   @get:Optional
   abstract val candidates: Property<String>

   @get:Option(option = "packages", description = "Specify the packages to scan for tests")
   @get:Input
   @get:Optional
   abstract val packages: Property<String>

   @get:Option(option = "tags", description = "Set tag expression to include or exclude tests")
   @get:Input
   @get:Optional
   abstract val tags: Property<String>

   /**
    * Returns the spec classes to include with the launcher command.
    */
   internal fun candidates(classpath: FileCollection): List<String> {
      // if the --candidates option was specified, then that is the highest priority and we take
      // that as a delimited list of fully qualified class names
      val candidatesFromOptions = candidates.orNull?.split(DELIMITER)
      if (candidatesFromOptions != null) return candidatesFromOptions

      // If specs was omitted, then we scan the classpath
      val specsFromScanning = TestClassDetector().detect(classpath.asFileTree)
      println("specsFromScanning: $specsFromScanning")

      // if packages was set, we filter down to only classes in those packages
      val packagesFromOptions = packages.orNull?.split(DELIMITER)?.toSet()
      val filteredSpecs = if (packagesFromOptions == null) {
         specsFromScanning
      } else {
         specsFromScanning.filter { spec ->
            packagesFromOptions.contains(spec.packageName)
         }
      }
      return filteredSpecs.map { it.qualifiedName }
   }

   companion object {
      const val DELIMITER = ";"
   }
}

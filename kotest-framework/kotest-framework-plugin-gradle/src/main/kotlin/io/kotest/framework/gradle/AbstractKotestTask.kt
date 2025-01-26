package io.kotest.framework.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.options.Option

open class AbstractKotestTask : DefaultTask() {

   private var tags: String? = null
   private var tests: String? = null
   private var candidates: String? = null
   private var packages: String? = null
   private var descriptor: String? = null

   companion object {
      const val DELIMITER = ";"
   }

   // gradle will call this if --descriptor was specified on the command line
   @Suppress("unused")
   @Option(option = "descriptor", description = "Filter to a single spec or test")
   fun setDescriptor(descriptor: String) {
      if (descriptor.isNotBlank())
         this.descriptor = descriptor
   }

   // gradle will call this if --tests was specified on the command line
   @Suppress("unused")
   @Option(option = "tests", description = "Filter to a test path expression")
   fun setTests(tests: String) {
      if (tests.isNotBlank())
         this.tests = tests
   }

   // gradle will call this if --candidates was specified on the command line
   @Suppress("unused")
   @Option(option = "candidates", description = "The candidates list to avoid scanning")
   fun setCandidates(candidates: String) {
      if (candidates.isNotBlank())
         this.candidates = candidates
   }

   // gradle will call this if --packages was specified on the command line
   @Suppress("unused")
   @Option(option = "packages", description = "Specify the packages to scan for tests")
   fun setPackages(packages: String) {
      if (packages.isNotBlank())
         this.packages = packages
   }

   // gradle will call this if --tags was specified on the command line
   @Suppress("unused")
   @Option(option = "tags", description = "Set tag expression to include or exclude tests")
   fun setTags(tags: String) {
      if (tags.isNotBlank())
         this.tags = tags
   }

   fun tags(): String? = tags
   fun descriptor(): String? = descriptor

   /**
    * Returns the spec classes to include with the launcher command.
    */
   internal fun candidates(classpath: FileCollection): List<String> {
      // if the --candidates option was specified, then that is the highest priority and we take
      // that as a delimited list of fully qualified class names
      val candidatesFromOptions = candidates?.split(DELIMITER)
      if (candidatesFromOptions != null) return candidatesFromOptions

      // If specs was omitted, then we scan the classpath
      val specsFromScanning = TestClassDetector().detect(classpath.asFileTree)
      println("specsFromScanning: $specsFromScanning")

      // if packages was set, we filter down to only classes in those packages
      val packagesFromOptions = packages?.split(DELIMITER)?.toSet()
      val filteredSpecs = if (packagesFromOptions == null) specsFromScanning else specsFromScanning.filter { spec ->
         packagesFromOptions.contains(spec.packageName)
      }
      return filteredSpecs.map { it.qualifiedName }
   }
}

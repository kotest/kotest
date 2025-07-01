package io.kotest.framework.gradle

import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property

object SpecsResolver {

   const val DELIMITER = ";"

   /**
    * Returns the spec classes to include with the launcher command.
    */
   internal fun specs(
      specs: Property<String>,
      packages: Property<String>,
      classpath: FileCollection
   ): List<String> {

      // if the --specs option was specified to the gradle task, then that is the highest priority and we take
      // that as a delimited list of fully qualified class names
      val specsFromCommandLine = specs.orNull?.split(DELIMITER)
      if (specsFromCommandLine != null) return specsFromCommandLine

      // If --specs was omitted, then we scan the classpath
      val specsFromScanning = TestClassDetector().detect(classpath.asFileTree)

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
}

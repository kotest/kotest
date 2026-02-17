package io.kotest.engine.config

import io.kotest.common.KotestInternal

@KotestInternal
object PackageUtils {

   /**
    * Returns the longest common package prefix shared by all strings in the collection,
    * where packages are separated by dots.
    *
    * For example, given "com.example.sam" and "com.example.sap", the common package prefix
    * is "com.example" (not "com.example.sa").
    *
    * If the collection is empty, it throws an [IllegalArgumentException].
    * If the collection contains only one element, returns that element.
    *
    * @param strings the collection of dot-separated package names to analyze
    * @return the longest common package prefix shared by all strings, or an empty string if none exists
    */
   fun commonPrefix(strings: Collection<String>): String {
      require(strings.isNotEmpty())
      if (strings.size == 1) return strings.first()

      val splitStrings = strings.map { it.split(".") }
      val first = splitStrings.first()
      val minSegments = splitStrings.minOf { it.size }

      var commonCount = 0
      for (i in 0 until minSegments) {
         if (splitStrings.all { it[i] == first[i] }) {
            commonCount++
         } else {
            break
         }
      }

      return first.take(commonCount).joinToString(".")
   }

   /**
    * Returns all parent packages for a given package name, including the package itself.
    *
    * For example, given "org.package.service", this returns a set containing:
    * - "org.package.service"
    * - "org.package"
    * - "org"
    *
    * If the package name is empty or contains no dots, returns a set with just the package name.
    *
    * @param packageName the fully qualified package name
    * @return a set of all parent packages including the package itself
    */
   fun parentPackages(packageName: String): Set<String> {
      if (packageName.isEmpty()) return emptySet()

      val packages = mutableSetOf<String>()
      packages.add(packageName)

      var current = packageName
      while (current.contains('.')) {
         val lastDotIndex = current.lastIndexOf('.')
         current = current.substring(0, lastDotIndex)
         packages.add(current)
      }

      return packages
   }
}

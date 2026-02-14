package io.kotest.engine.config

import io.kotest.common.KotestInternal

@KotestInternal
object PackageUtils {

   /**
    * Returns the common prefix shared by all strings in the collection.
    *
    * If the collection is empty, it returns an empty string.
    * If the collection contains only one element, returns that element.
    *
    * @param strings the collection of strings to analyze
    * @return the longest common prefix shared by all strings, or an empty string if none exists
    */
   fun commonPrefix(strings: Collection<String>): String {
      require(strings.isNotEmpty())
      if (strings.size == 1) return strings.first()

      val first = strings.first()
      var prefixLength = first.length

      for (string in strings.drop(1)) {
         prefixLength = minOf(prefixLength, string.length)
         for (i in 0 until prefixLength) {
            if (first[i] != string[i]) {
               prefixLength = i
               break
            }
         }
         if (prefixLength == 0) return ""
      }

      return first.substring(0, prefixLength)
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

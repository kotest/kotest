package io.kotest.runner.junit.platform.gradle

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.mpp.Logger

class GradleClassMethodRegexTestFilter(private val patterns: List<String>) : TestFilter {

   private val logger = Logger(GradleClassMethodRegexTestFilter::class)

   override fun filter(descriptor: Descriptor): TestFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against $patterns") }
      return when {
         patterns.isEmpty() -> TestFilterResult.Include
         patterns.any { match(it, descriptor) } -> TestFilterResult.Include
         else -> TestFilterResult.Exclude(null)
      }
   }

   private fun match(pattern: String, descriptor: Descriptor): Boolean {
      val (prefixWildcard, pck, classname, path) = GradleTestPattern.parse(pattern)
      return when (descriptor) {
         is Descriptor.TestDescriptor -> when (path) {
           null -> false
           else -> descriptor.path(false).value.startsWith(path)
         }

         is Descriptor.SpecDescriptor -> when {
            pck != null && classname != null && prefixWildcard -> descriptor.kclass.qualifiedName?.contains("$pck.$classname") ?: false
            pck != null && classname != null -> descriptor.kclass.qualifiedName == "$pck.$classname"
            pck != null && prefixWildcard -> descriptor.kclass.qualifiedName?.contains(pck) ?: true
            pck != null -> descriptor.kclass.qualifiedName?.startsWith(pck) ?: true
            classname != null && prefixWildcard -> descriptor.kclass.simpleName?.contains(classname) ?: false
            classname != null -> descriptor.kclass.simpleName == classname
            else -> true
         }
      }
   }
}

data class GradleTestPattern(
   val prefixWildcard: Boolean,
   val pckage: String?,
   val classname: String?,
   val path: String?,
) {
   companion object {

      // if the regex starts with a lower case character, then we assume it is in the format package.Class.testpath
      // otherwise, we assume it is in the format Class.testpath
      // the .testpath is always optional, and at least Class or package must be specified
      // additionally, patterns can start with a * in which case the pattern matches suffixes
      fun parse(pattern: String): GradleTestPattern {
         require(pattern.isNotBlank())

         val prefixWildcard = pattern.startsWith("*")
         val pattern2 = pattern.removePrefix("*.").removePrefix("*")

         val tokens = pattern2.split('.')
         val classIndex = tokens.indexOfFirst { it.first().isUpperCase() }

         // if class is not specified, then we assume the entire string is a package
         if (classIndex == -1) return GradleTestPattern(prefixWildcard, pattern2, null, null)

         // if the class is the first part, then no package is specified
         val pck = if (classIndex == 0) null else tokens.take(classIndex).joinToString(".")

         val pathParts = tokens.drop(classIndex + 1)
         val path = if (pathParts.isEmpty()) null else pathParts.joinToString(".")

         return GradleTestPattern(prefixWildcard, pck, tokens[classIndex], path)
      }

   }
}

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

   fun match(pattern: String, descriptor: Descriptor): Boolean {
      val (pck, classname, path) = GradleTestPattern.parse(pattern)
      return when (descriptor) {
         is Descriptor.TestDescriptor -> descriptor.parts().take(path.size) == path
         is Descriptor.SpecDescriptor -> when {
            pck != null && classname != null -> descriptor.kclass.qualifiedName == "$pck.$classname"
            pck != null -> descriptor.kclass.qualifiedName?.startsWith(pck) ?: true
            classname != null -> descriptor.kclass.simpleName == classname
            else -> true
         }
      }
   }
}

data class GradleTestPattern(val pckage: String?, val classname: String?, val path: List<String>) {
   companion object {

      // if the regex starts with a lower case character, then we assume it is in the format package.Class.testpath
      // otherwise, we assume it is in the format Class.testpath
      // the .testpath is always optional, and at least Class or package must be specified
      fun parse(pattern: String): GradleTestPattern {

         val tokens = pattern.split('.')
         val classIndex = tokens.indexOfFirst { it.first().isUpperCase() }

         // if class is not specified, then we assume the entire string is a package
         if (classIndex == -1) return GradleTestPattern(pattern, null, emptyList())

         // if the class is the first part, then no package is specified
         val pck = if (classIndex == 0) null else tokens.take(classIndex).joinToString(".")

         val pathParts = tokens.drop(classIndex + 1)
         val path = if (pathParts.isEmpty()) emptyList() else pathParts.joinToString(".").split(Descriptor.TestDelimiter)

         return GradleTestPattern(pck, tokens[classIndex], path)
      }

   }
}

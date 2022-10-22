package io.kotest.runner.junit.platform.gradle

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.TestPath
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.mpp.Logger

class GradleClassMethodRegexTestFilter(private val patterns: List<String>) : TestFilter {

   private val logger = Logger(GradleClassMethodRegexTestFilter::class)

   override fun filter(descriptor: Descriptor): TestFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against $patterns") }
      return when {
         patterns.isEmpty() -> TestFilterResult.Include
         patterns.all { match(it, descriptor) } -> TestFilterResult.Include
         else -> TestFilterResult.Exclude(null)
      }
   }

   /**
    * Matches the pattern supplied from gradle build script or command line interface.
    * Gradle supplies a well-formed regex to the engine that we can leverage to construct a well-formed regex object.
    *
    * - A* becomes \QA\E.*
    * - A*Test becomes \QA\E.*\QTest\E
    * - io.*.A*Test becomes \Qio.\E.*\Q.A\E.*\QTest\E
    * - io.*.A*Test.AccountDetails* becomes \Qio.\E.*\Q.A\E.*\QTest.AccountDetails\E.*
    * - io.*.A*Test.some test context* becomes \Qio.\E.*\Q.A\E.*\QTest.some test context\E.*
    */
   private fun match(pattern: String, descriptor: Descriptor): Boolean {
      val path = descriptor.dotSeparatedFullPath().value
      val regexPattern = "^(.*)$pattern".toRegex() // matches
      val laxRegexPattern = "^(.*)$pattern(.*)\$".toRegex() // matches pattern that begins with and followed by
      val packagePath = descriptor.spec().kclass.java.packageName // io.kotest

      val isSimpleClassMatch = descriptor.spec().kclass.java.simpleName.matches(pattern.toRegex()) // SomeTest or *Test
      val isSpecMatched = descriptor.spec().id.value.matches(regexPattern) // *.SomeTest
      val isFullPathMatched = path.matches(regexPattern) // io.*.SomeTest
      val isFullPathDotMatched = "$path.".matches(regexPattern) // io.*. or io.*.SomeTest.*

      val doesNotContainUppercase = pattern.replace("\\Q", "").replace("\\E", "").all { !it.isUpperCase() }

      val isPackageMatched = doesNotContainUppercase && packagePath.matches(laxRegexPattern) // io.kotest
      val isPackageWithDotMatched = doesNotContainUppercase && "$packagePath.".matches(laxRegexPattern) // io.kotest.*

      return isSimpleClassMatch ||
         isFullPathMatched ||
         isFullPathDotMatched ||
         isSpecMatched ||
         isPackageMatched ||
         isPackageWithDotMatched
   }

   /**
    * Returns a gradle-compatible dot-separated full path of the given descriptor.
    */
   private fun Descriptor.dotSeparatedFullPath(): TestPath = when (this) {
      is Descriptor.SpecDescriptor -> TestPath(this.id.value)
      is Descriptor.TestDescriptor -> when (this.parent) {
         is Descriptor.SpecDescriptor -> TestPath("${this.parent.id.value}.${this.id.value}")
         is Descriptor.TestDescriptor -> TestPath("${this.parent.dotSeparatedFullPath().value} -- ${this.id.value}")
      }
   }
}

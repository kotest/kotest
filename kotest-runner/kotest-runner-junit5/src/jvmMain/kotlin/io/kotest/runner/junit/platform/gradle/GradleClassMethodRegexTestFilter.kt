package io.kotest.runner.junit.platform.gradle

import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.descriptors.Descriptor
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.core.Logger

internal class GradleClassMethodRegexTestFilter(private val patterns: List<String>) : DescriptorFilter {

   private val logger = Logger(GradleClassMethodRegexTestFilter::class)

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against $patterns") }
      return when {
         patterns.isEmpty() -> DescriptorFilterResult.Include
         patterns.any { match(it, descriptor) } -> DescriptorFilterResult.Include
         else -> DescriptorFilterResult.Exclude(null)
      }
   }

   /**
    * Matches the pattern supplied from gradle build script or command line interface.
    *
    * supports:
    * - gradle test --tests "SomeTest"
    * - gradle test --tests "*Test"
    * - gradle test --tests "io.package.*"
    * - gradle test --tests "io.package"
    * - gradle test --tests "io.package.SomeTest"
    * - gradle test --tests "io.package.SomeTest.first level context*"
    * - gradle test --tests "io.package.SomeTest.*"
    * - gradle test --tests "io.*.SomeTest"
    * - gradle test --tests "SomeTest.first level context*"
    * - gradle test --tests "*.first level context*"
    *
    * Exact nested context / test matching is NOT CURRENTLY SUPPORTED.
    * Kotest support lazy test registration within nested context. Gradle test filter does not
    * natively work nicely with kotest. In order to make it work we need to think of a way to
    * recursively apply partial context-search as we dive deeper into the contexts.
    *
    * Notes to Maintainers:
    *
    * Gradle supplies a pattern string which corresponds to a well-formed regex object.
    * This can be directly usable for kotest.
    * - A* becomes \QA\E.*
    * - A*Test becomes \QA\E.*\QTest\E
    * - io.*.A*Test becomes \Qio.\E.*\Q.A\E.*\QTest\E
    * - io.*.A*Test.AccountDetails* becomes \Qio.\E.*\Q.A\E.*\QTest.AccountDetails\E.*
    * - io.*.A*Test.some test context* becomes \Qio.\E.*\Q.A\E.*\QTest.some test context\E.*
    */
   private fun match(pattern: String, descriptor: Descriptor): Boolean {
      val path = descriptor.dotSeparatedFullPath().value
      val regexPattern = "^(.*)$pattern".toRegex() // matches pattern exactly
      val laxRegexPattern = "^(.*)$pattern(.*)\$".toRegex() // matches pattern that can be followed by others
      val packagePath = descriptor.spec().id.value.split(".").dropLast(1).joinToString(".") // io.kotest

      val isSimpleClassMatch by lazy {
         // SomeTest or *Test
         descriptor.spec().id.value.split(".").lastOrNull()?.matches(pattern.toRegex()) == true
      }
      val isSpecMatched by lazy { descriptor.spec().id.value.matches(regexPattern) } // *.SomeTest
      val isFullPathMatched by lazy { path.matches(regexPattern) } // io.*.SomeTest
      val isFullPathDotMatched by lazy { "$path.".matches(regexPattern) } // io.*. or io.*.SomeTest.*

      // if there's no uppercase in the supplied pattern, activate trigger relaxed matching
      val doesNotContainUppercase by lazy { pattern.replace("\\Q", "").replace("\\E", "").all { !it.isUpperCase() } }

      val isPackageMatched by lazy { doesNotContainUppercase && packagePath.matches(laxRegexPattern) } // io.kotest
      val isPackageWithDotMatched by lazy { doesNotContainUppercase && "$packagePath.".matches(laxRegexPattern) } // io.kotest.*

      return isSimpleClassMatch ||
         isFullPathMatched ||
         isFullPathDotMatched ||
         isSpecMatched ||
         isPackageMatched ||
         isPackageWithDotMatched
   }

   /**
    * Returns a gradle-compatible dot-separated full path of the given descriptor.
    * i.e. io.package.MyTest.given something -- should do something
    *
    * Note: I'm forced to do this... :(
    *
    * We cannot use the / separator for contexts as gradle rejects that.
    * Filters also seemingly only works on first "." after the class. This was severely limiting.
    * The other problem is that also means we can't have "." in the test / context path because gradle doesn't
    * like it and will not even give us any candidate classes.
    */
   private fun Descriptor.dotSeparatedFullPath(): DescriptorPath = when (this) {
      is Descriptor.SpecDescriptor -> DescriptorPath(this.id.value)
      is Descriptor.TestDescriptor -> when (this.parent) {
         is Descriptor.SpecDescriptor -> DescriptorPath("${this.parent.id.value}.${this.id.value}")
         is Descriptor.TestDescriptor -> DescriptorPath("${this.parent.dotSeparatedFullPath().value} -- ${this.id.value}")
      }
   }
}

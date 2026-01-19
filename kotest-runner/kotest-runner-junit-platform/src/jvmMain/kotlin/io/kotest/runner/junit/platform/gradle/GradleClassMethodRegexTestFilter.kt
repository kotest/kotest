package io.kotest.runner.junit.platform.gradle

import io.kotest.common.env
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorPath
import io.kotest.engine.extensions.filter.DescriptorFilter
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.engine.extensions.filter.INCLUDE_PATTERN_ENV

internal class GradleClassMethodRegexTestFilter(private val patterns: Set<String>) : DescriptorFilter {

   private val logger = Logger(GradleClassMethodRegexTestFilter::class)

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against $patterns") }
      val env = env(INCLUDE_PATTERN_ENV)
      return when {
         // when we have the INCLUDE_PATTERN_ENV set, that means the Kotest plugin has forwarded the --tests arg
         // in the form of an env variable. So we will use that to take priority and ignore --tests here
         env != null -> DescriptorFilterResult.Include
         patterns.isEmpty() -> DescriptorFilterResult.Include
         patterns.any { match(it, descriptor) } -> DescriptorFilterResult.Include
         else -> DescriptorFilterResult.Exclude(null)
      }
   }

   /**
    * Matches the pattern supplied from the Gradle build script or command line interface.
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
      val laxRegexPattern = "^(.*)$pattern(.*)$".toRegex() // matches pattern that can be followed by others
      val packagePath = descriptor.spec().id.value.split(".").dropLast(1).joinToString(".") // io.kotest

      val isSimpleClassMatch by lazy {
         // SomeTest or *Test
         descriptor.spec().id.value.split(".").lastOrNull()?.matches(pattern.toRegex()) == true
      }

      // matches a spec descriptor when we have a spec + method pattern
      val isSpecPrefix by lazy {
         if (descriptor !is Descriptor.SpecDescriptor) false
         else pattern.removePrefix("\\Q").removeSuffix("\\E").startsWith(descriptor.id.value)
      }

      val isSpecMatched by lazy { descriptor.spec().id.value.matches(regexPattern) } // *.SomeTest
      val isFullPathMatched by lazy { path.matches(regexPattern) } // io.*.SomeTest
      val isFullPathDotMatched by lazy { "$path.".matches(regexPattern) } // io.*. or io.*.SomeTest.*

      // if there's no uppercase in the supplied pattern, activate trigger relaxed matching
      val doesNotContainUppercase by lazy { pattern.replace("\\Q", "").replace("\\E", "").all { !it.isUpperCase() } }

      val isPackageMatched by lazy { doesNotContainUppercase && packagePath.matches(laxRegexPattern) } // io.kotest
      val isPackageWithDotMatched by lazy { doesNotContainUppercase && "$packagePath.".matches(laxRegexPattern) } // io.kotest.*

      // Check if this descriptor is a descendant of the pattern target.
      // This ensures nested tests are included when filtering to a parent context.
      // E.g., when filtering to "SomeSpec.context name", the nested test "SomeSpec.context name -- nested test"
      // should also be included.
      val isDescendantOfPattern by lazy {
         // The pattern might match a prefix of this path (meaning this path is a descendant)
         // We check if the path matches the pattern followed by " -- " (nested test separator) and more content
         val descendantRegex = "^(.*)$pattern -- (.+)$".toRegex()
         path.matches(descendantRegex)
      }

      return isSimpleClassMatch ||
         isFullPathMatched ||
         isFullPathDotMatched ||
         isSpecMatched ||
         isSpecPrefix ||
         isPackageMatched ||
         isPackageWithDotMatched ||
         isDescendantOfPattern
   }

   /**
    * Returns a gradle-compatible dot-separated full path of the given descriptor.
    * i.e. io.package.MyTest.given something -- should do something
    *
    * Note: I'm forced to do this... :(
    *
    * We cannot use the / separator for contexts as Gradle rejects that.
    * Filters also seemingly only works on first "." after the class. This was severely limiting.
    * The other problem is that also means we can't have "." in the test / context path because Gradle doesn't
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

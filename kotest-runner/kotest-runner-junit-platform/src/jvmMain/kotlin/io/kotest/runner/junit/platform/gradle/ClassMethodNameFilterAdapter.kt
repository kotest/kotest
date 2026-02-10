@file:Suppress("KDocUnresolvedReference")

package io.kotest.runner.junit.platform.gradle

import io.kotest.engine.extensions.filter.DescriptorFilter
import io.kotest.runner.junit.platform.postFilters
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.launcher.PostDiscoveryFilter

/**
 * JUnit has this concept of [PostDiscoveryFilter]s which can be applied after test discovery.
 *
 * Gradle implements the cli parameter "--tests Foo.mytest" by passing an instance of
 * [org.gradle.api.internal.tasks.testing.junitplatform.ClassMethodNameFilter] which is an
 * implementation of PostDiscoveryFilter. It is also used by the test retry plugin.
 *
 * This adapter returns [DescriptorFilter]s for these filters.
 */
internal object ClassMethodNameFilterAdapter {

   /**
    * Returns [DescriptorFilter]s adapted from [PostDiscoveryFilter]s that are
    * implementations of [ClassMethodNameFilter].
    *
    * If the format contains a nested test name, then we use a special Kotest parsed version, otherwise
    * we use a wrapper around the Gradle filter.
    *
    * All patterns from multiple `--tests` arguments are grouped into a single filter per type
    * (regex-based or nested-test-based) so that they are combined with OR logic.
    * This is necessary because the engine applies multiple [DescriptorFilter]s with AND semantics,
    * but multiple `--tests` arguments should match if ANY pattern matches.
    *
    * If no post-filters are present, this will return an empty list.
    */
   internal fun adapt(request: EngineDiscoveryRequest): List<DescriptorFilter> {
      val patterns = ClassMethodNameFilterUtils.extractIncludePatterns(request.postFilters())
      if (patterns.isEmpty()) return emptyList()

      val nestedArgs = mutableSetOf<NestedTestArg>()
      val regexPatterns = mutableSetOf<String>()

      for (pattern in patterns) {
         val nestedTestArg = NestedTestsArgParser.parse(pattern)
         if (nestedTestArg != null) {
            nestedArgs.add(nestedTestArg)
         } else {
            regexPatterns.add(pattern)
         }
      }

      // HACK since we have a tests filter with a nested test name, we will clear the list of post-filters
      // so Gradle doesn't do any filtering - otherwise, Gradle will incorrectly filter out the nested
      // test as it doesn't understand the kotest format
      if (nestedArgs.isNotEmpty()) {
         ClassMethodNameFilterUtils.reset(request.postFilters())
      }

      // Return a single combined filter when both types are present, to preserve OR semantics.
      // When only one type is present, return a single filter of that type.
      return when {
         nestedArgs.isEmpty() -> listOf(GradleClassMethodRegexTestFilter(regexPatterns))
         regexPatterns.isEmpty() -> listOf(NestedTestsArgDescriptorFilter(nestedArgs))
         else -> listOf(CombinedGradleDescriptorFilter(regexPatterns, nestedArgs))
      }
   }
}

@file:Suppress("KDocUnresolvedReference")

package io.kotest.runner.junit.platform.gradle

import io.kotest.core.descriptors.Descriptor
import io.kotest.engine.extensions.filter.DescriptorFilter
import io.kotest.engine.extensions.filter.DescriptorFilterResult
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
 * This adapter will return a [DescriptorFilter] for each of these filters.
 */
internal object ClassMethodNameFilterAdapter {

   /**
    * Returns a [DescriptorFilter] for each [PostDiscoveryFilter] that is an
    * implementation of [ClassMethodNameFilter].
    *
    * If the format contains a nested test name, then we use a special Kotest parsed version, otherwise
    * we use a wrapper around the Gradle filter.
    *
    * If no post-filters are present, this will return an empty list.
    */
   internal fun adapt(request: EngineDiscoveryRequest): List<DescriptorFilter> {
      val patterns = ClassMethodNameFilterUtils.extractIncludePatterns(request.postFilters())
      if (patterns.isEmpty()) {
         return emptyList()
      }

      val nestedArgs = mutableSetOf<NestedTestArg>()
      val regexPatterns = mutableSetOf<String>()

      for (filter in patterns) {
         val nestedTestArg = NestedTestsArgParser.parse(filter)
         if (nestedTestArg != null) {
            nestedArgs.add(nestedTestArg)
         } else {
            regexPatterns.add(filter)
         }
      }

      if (nestedArgs.isNotEmpty()) {
         // HACK since we have a tests filter with a nested test name, we will clear the list of post-filters
         // so Gradle doesn't do any filtering - otherwise, Gradle will incorrectly filter out the nested
         // test as it doesn't understand the kotest format
         ClassMethodNameFilterUtils.reset(request.postFilters())
      }

      val descriptorFilter = when {
         nestedArgs.isEmpty() -> GradleClassMethodRegexTestFilter(regexPatterns)
         regexPatterns.isEmpty() -> NestedTestsArgDescriptorFilter(nestedArgs)
         else -> object : DescriptorFilter {
            override fun filter(descriptor: Descriptor): DescriptorFilterResult {
               val descriptorFilters = listOf(
                  GradleClassMethodRegexTestFilter(regexPatterns),
                  NestedTestsArgDescriptorFilter(nestedArgs),
               )
               return if (descriptorFilters.any { it.filter(descriptor) == DescriptorFilterResult.Include }) {
                  DescriptorFilterResult.Include
               } else {
                  DescriptorFilterResult.Exclude(null)
               }
            }
         }
      }

      return listOf(descriptorFilter)
   }
}

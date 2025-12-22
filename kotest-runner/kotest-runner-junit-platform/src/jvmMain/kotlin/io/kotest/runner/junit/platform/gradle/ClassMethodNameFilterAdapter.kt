@file:Suppress("KDocUnresolvedReference")

package io.kotest.runner.junit.platform.gradle

import io.kotest.engine.extensions.DescriptorFilter
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
    * we use a wrapper around the gradle filter.
    *
    * If no post filters are present, this will return an empty list.
    */
   internal fun adapt(request: EngineDiscoveryRequest): List<DescriptorFilter> {
      return ClassMethodNameFilterUtils.extractIncludePatterns(request.postFilters())
         .map { filter ->
            val nestedTestArg = NestedTestsArgParser.parse(filter)
            if (nestedTestArg != null) {
               // HACK since we have a tests filter with nested test name, we will clear the list of post filters
               // so gradle doesn't do any filtering - otherwise, gradle will incorrectly filter out the nested
               // test as it doesn't understand the kotest format
               // note - this implementation assumes if we have one nested post filter, then there are no others
               ClassMethodNameFilterUtils.reset(request.postFilters())
               NestedTestsArgDescriptorFilter(setOf(nestedTestArg))
            } else
               GradleClassMethodRegexTestFilter(setOf(filter))
         }
   }
}

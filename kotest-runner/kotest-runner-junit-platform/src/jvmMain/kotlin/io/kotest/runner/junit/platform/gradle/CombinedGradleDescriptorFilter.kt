package io.kotest.runner.junit.platform.gradle

import io.kotest.core.descriptors.Descriptor
import io.kotest.engine.extensions.filter.DescriptorFilter
import io.kotest.engine.extensions.filter.DescriptorFilterResult

/**
 * A [DescriptorFilter] that combines both regex-based and nested-test-based filters with OR logic.
 *
 * This is used when multiple `--tests` arguments contain a mix of simple patterns and nested test patterns.
 * A descriptor is included if it matches ANY of the regex patterns OR ANY of the nested test args.
 */
internal class CombinedGradleDescriptorFilter(
   regexPatterns: Set<String>,
   nestedArgs: Set<NestedTestArg>,
) : DescriptorFilter {

   private val regexFilter = GradleClassMethodRegexTestFilter(regexPatterns)
   private val nestedFilter = NestedTestsArgDescriptorFilter(nestedArgs)

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      if (regexFilter.filter(descriptor) is DescriptorFilterResult.Include) return DescriptorFilterResult.Include
      if (nestedFilter.filter(descriptor) is DescriptorFilterResult.Include) return DescriptorFilterResult.Include
      return DescriptorFilterResult.Exclude(null)
   }
}

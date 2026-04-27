package io.kotest.engine.extensions.filter

import io.kotest.common.env
import io.kotest.core.descriptors.Descriptor

const val INCLUDE_PATTERN_ENV = "KOTEST_INCLUDE_PATTERN"

/**
 * An implementation of [DescriptorFilter] that uses an include pattern from an env variable.
 */
internal object IncludePatternEnvDescriptorFilter : TestPatternIncludeDescriptorFilter() {

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      val env = env(INCLUDE_PATTERN_ENV)
      // if there is no include pattern, then we include everything by default
      if (env.isNullOrBlank()) return DescriptorFilterResult.Include

      // the Kotest Gradle plugin will merge multiple --test args to a single env var, so we must split here
      // Gradle behavior: Cumulative Filtering: Each --tests option acts as an inclusive filter. For example, running gradle test --tests "com.packageA.*" --tests "com.packageB.*" will execute all tests in both packages
      val args = env.split(";")
      val any = args.any { filter(it, descriptor) == DescriptorFilterResult.Include }
      return if (any) DescriptorFilterResult.Include else DescriptorFilterResult.Exclude(null)
   }
}

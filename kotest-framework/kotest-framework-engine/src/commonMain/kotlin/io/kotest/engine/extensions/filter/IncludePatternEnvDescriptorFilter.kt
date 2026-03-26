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
      return if (env.isNullOrBlank()) DescriptorFilterResult.Include else filter(env, descriptor)
   }
}

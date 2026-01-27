package io.kotest.runner.junit4

import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.core.descriptors.Descriptor
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.engine.extensions.filter.TestPatternIncludeDescriptorFilter

const val INSTRUMENTATION_INCLUDE_PATTERN_ARG = "INSTRUMENTATION_INCLUDE_PATTERN"

/**
 * An implementation of [TestPatternIncludeDescriptorFilter] that fetches its filter pattern
 * from Instrumentation arguments
 *
 * Android docs:
 * You can pass custom parameters (e.g., -e server_url https://api.test.com) and retrieve them within your test
 * code using InstrumentationRegistry.getArguments().getString("server_url"
 */
object InstrumentationFilter : TestPatternIncludeDescriptorFilter() {
   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      val pattern = runCatching {
         InstrumentationRegistry.getArguments()?.getString(INSTRUMENTATION_INCLUDE_PATTERN_ARG)
      }.getOrNull()
      // if there is no include pattern, then we include everything by default
      return if (pattern.isNullOrBlank()) DescriptorFilterResult.Include else filter(pattern, descriptor)
   }
}

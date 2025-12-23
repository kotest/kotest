package io.kotest.engine.extensions

import io.kotest.common.env
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId

/**
 * An implementation of [DescriptorFilter] that uses an include pattern from an env variable.
 */
internal object IncludeTestPatternDescriptorFilter : DescriptorFilter {

   const val ENV_NAME = "KOTEST_INCLUDE_PATTERN"

   private val logger = Logger(IncludeTestPatternDescriptorFilter::class)

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      val env = env(ENV_NAME)
      // if no include pattern then we include everything by default
      return if (env.isNullOrBlank()) DescriptorFilterResult.Include else filter(env, descriptor)
   }

   internal fun filter(env: String, descriptor: Descriptor): DescriptorFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against include pattern $env") }
      val pattern = TestPatternParser.parse(env)
      return if (match(pattern, descriptor)) DescriptorFilterResult.Include else DescriptorFilterResult.Exclude(null)
   }

   private fun match(pattern: TestPattern, descriptor: Descriptor): Boolean {
      logger.log { Pair(descriptor.toString(), "Testing pattern $pattern against $descriptor") }
      if (pattern.className == null) return packageMatch(pattern, descriptor)
      val spec: Descriptor = Descriptor.SpecDescriptor(DescriptorId(pattern.packageName + "." + pattern.className))
      val tests = pattern.contexts.fold(spec) { acc, op -> acc.append(op) }
      return descriptor.hasSharedPath(tests)
   }

   private fun packageMatch(
      pattern: TestPattern,
      descriptor: Descriptor
   ): Boolean {
      val pck = descriptor.id.value.substringBeforeLast(".")
      return if (pattern.subpackages) pck.startsWith(pattern.packageName) else pck == pattern.packageName
   }
}

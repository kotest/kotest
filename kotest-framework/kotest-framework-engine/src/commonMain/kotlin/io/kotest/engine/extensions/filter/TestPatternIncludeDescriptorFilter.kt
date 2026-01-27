package io.kotest.engine.extensions.filter

import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.engine.extensions.TestPattern
import io.kotest.engine.extensions.TestPatternParser

abstract class TestPatternIncludeDescriptorFilter : DescriptorFilter {

   private val logger = Logger(TestPatternIncludeDescriptorFilter::class)

   fun filter(pattern: String, descriptor: Descriptor): DescriptorFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against include pattern $pattern") }
      val testPattern = TestPatternParser.parse(pattern)
      return if (match(testPattern, descriptor))
         DescriptorFilterResult.Include
      else
         DescriptorFilterResult.Exclude(null)
   }

   private fun match(pattern: TestPattern, descriptor: Descriptor): Boolean {
      logger.log { Pair(descriptor.toString(), "Testing pattern $pattern against $descriptor") }
      if (pattern.className == null) return packageMatch(pattern, descriptor.spec())
      val spec: Descriptor = Descriptor.SpecDescriptor(DescriptorId(pattern.packageName + "." + pattern.className))
      val tests = pattern.contexts.fold(spec) { acc, op -> acc.append(op) }
      return descriptor.hasSharedPath(tests)
   }

   private fun packageMatch(
      pattern: TestPattern,
      descriptor: Descriptor.SpecDescriptor
   ): Boolean {
      val pck = descriptor.id.value.substringBeforeLast(".") // grabs just the package by dropping the class name
      return if (pattern.subpackages) pck.startsWith(pattern.packageName) else pck == pattern.packageName
   }
}

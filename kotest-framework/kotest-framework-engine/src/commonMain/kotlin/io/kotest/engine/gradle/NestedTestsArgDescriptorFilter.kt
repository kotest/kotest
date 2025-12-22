package io.kotest.engine.gradle

import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.extensions.DescriptorFilterResult

/**
 * An implementation of [io.kotest.engine.extensions.DescriptorFilter] that adapts the gradle --tests arg
 * when the value contains a nested test.
 */
@KotestInternal
class NestedTestsArgDescriptorFilter(private val args: Set<NestedTestArg>) : DescriptorFilter {

   private val logger = Logger(NestedTestsArgDescriptorFilter::class)

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against $args") }
      return when {
         args.isEmpty() -> DescriptorFilterResult.Include
         args.any { match(it, descriptor) } -> DescriptorFilterResult.Include
         else -> DescriptorFilterResult.Exclude(null)
      }
   }

   private fun match(arg: NestedTestArg, descriptor: Descriptor): Boolean {
      logger.log { Pair(descriptor.toString(), "Testing $arg against $descriptor") }
      val spec: Descriptor = Descriptor.SpecDescriptor(DescriptorId(arg.packageName + "." + arg.className))
      val tests = arg.contexts.fold(spec) { acc, op -> acc.append(op) }
      return descriptor.hasSharedPath(tests)
   }
}

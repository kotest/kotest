package io.kotest.engine.gradle

import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.extensions.DescriptorFilterResult

/**
 * An implementation of [io.kotest.engine.extensions.DescriptorFilter] that adapts the gradle --tests option
 * for use when being invoked from inside the kotest intellij plugin.
 */
@KotestInternal
class IntellijGradleTestsArgDescriptorFilter(private val args: Set<TestArg>) : DescriptorFilter {

   private val logger = Logger(IntellijGradleTestsArgDescriptorFilter::class)

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against $args") }
      return when {
         args.isEmpty() -> DescriptorFilterResult.Include
         args.any { match(it, descriptor) } -> DescriptorFilterResult.Include
         else -> DescriptorFilterResult.Exclude(null)
      }
   }

   private fun match(arg: TestArg, descriptor: Descriptor): Boolean {
      logger.log { Pair(descriptor.toString(), "Testing $arg against $descriptor") }
      return when (arg) {
         is TestArg.Package -> descriptor.spec().id.value.startsWith(arg.packageName)
         is TestArg.Class -> descriptor.spec().id.value == arg.fqn
         is TestArg.Test -> {
            val spec: Descriptor = Descriptor.SpecDescriptor(DescriptorId(arg.fqn))
            val tests = arg.contexts.fold(spec) { acc, op -> acc.append(op) }
            descriptor.hasSharedPath(tests)
         }
      }
   }
}

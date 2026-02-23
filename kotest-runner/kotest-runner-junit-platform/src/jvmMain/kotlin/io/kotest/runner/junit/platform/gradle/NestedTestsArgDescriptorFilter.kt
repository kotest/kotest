package io.kotest.runner.junit.platform.gradle

import io.kotest.common.env
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.engine.extensions.filter.DescriptorFilter
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.engine.extensions.filter.INCLUDE_PATTERN_ENV

/**
 * Strips newlines and trims surrounding whitespace from a test name so that
 * it can be matched against a normalized Gradle filter pattern.
 */
private fun String.normalizeTestName(): String =
   replace("\r\n", " ").replace("\n", " ").replace("\r", " ").trim()

/**
 * An implementation of [DescriptorFilter] that supports nested test names.
 */
internal class NestedTestsArgDescriptorFilter(private val args: Set<NestedTestArg>) : DescriptorFilter {

   private val logger = Logger(NestedTestsArgDescriptorFilter::class)

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against nested test args $args") }
      val env = env(INCLUDE_PATTERN_ENV)
      return when {
         // when we have the INCLUDE_PATTERN_ENV set, that means the Kotest plugin has forwarded the --tests arg
         // in the form of an env variable. So we will use that to take priority and ignore --tests here
         env != null -> DescriptorFilterResult.Include
         args.isEmpty() -> DescriptorFilterResult.Include
         args.any { match(it, descriptor) } -> DescriptorFilterResult.Include
         else -> DescriptorFilterResult.Exclude(null)
      }
   }

   private fun match(arg: NestedTestArg, descriptor: Descriptor): Boolean {
      logger.log { Pair(descriptor.toString(), "Testing $arg against $descriptor") }
      val spec: Descriptor = Descriptor.SpecDescriptor(DescriptorId(arg.packageName + "." + arg.className))
      val tests = arg.contexts.fold(spec) { acc, op -> acc.append(op) }
      return descriptor.normalized().hasSharedPath(tests)
   }

   /**
    * Returns a copy of this descriptor with newlines stripped and surrounding whitespace trimmed
    * from each id, so it can be matched against a normalized Gradle filter pattern.
    */
   private fun Descriptor.normalized(): Descriptor = when (this) {
      is Descriptor.SpecDescriptor -> Descriptor.SpecDescriptor(DescriptorId(this.id.value.normalizeTestName()))
      is Descriptor.TestDescriptor -> Descriptor.TestDescriptor(this.parent.normalized(), DescriptorId(this.id.value.normalizeTestName()))
   }
}

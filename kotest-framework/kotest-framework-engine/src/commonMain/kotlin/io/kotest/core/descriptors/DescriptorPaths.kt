package io.kotest.core.descriptors

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor.SpecDescriptor
import io.kotest.core.descriptors.Descriptor.TestDescriptor
import io.kotest.core.descriptors.DescriptorPaths.SPEC_DELIMITER
import io.kotest.core.descriptors.DescriptorPaths.TEST_DELIMITER
import io.kotest.core.descriptors.DescriptorPaths.render

/**
 * Utility functions for working with [Descriptor] paths.
 */
@KotestInternal
object DescriptorPaths {

   const val SPEC_DELIMITER = "/"
   const val TEST_DELIMITER = " -- "

   /**
    * Returns a string representation of the given [descriptor] wrapped in a [DescriptorPath].
    *
    * The spec name is followed by a [SPEC_DELIMITER] and then the test context. Each element in the test
    * context is separated by a [TEST_DELIMITER].
    *
    * Examples:
    *
    * com.sksamuel.MySpec
    * com.sksamuel.MySpec/a context
    * com.sksamuel.MySpec/a context -- a test
    * com.sksamuel.MySpec/a context -- another context -- another test
    */
   fun render(descriptor: Descriptor): DescriptorPath = render(descriptor, SPEC_DELIMITER, TEST_DELIMITER)

   /**
    * Returns a string representation of the given [descriptor] wrapped in a [DescriptorPath].
    *
    * The spec name is followed by a [specDelimiter] and then the test context. Each element in the test
    * context is separated by a [testDelimiter].
    *
    * Examples:
    *
    * com.sksamuel.MySpec
    * com.sksamuel.MySpec<specDelimiter>a context
    * com.sksamuel.MySpec<specDelimiter>a context <testDelimiter> a test
    */
   fun render(
      descriptor: Descriptor,
      specDelimiter: String,
      testDelimiter: String,
   ): DescriptorPath = when (descriptor) {
      is SpecDescriptor -> DescriptorPath(descriptor.id.value)
      is TestDescriptor -> when (val p = descriptor.parent) {
         is SpecDescriptor -> DescriptorPath(p.id.value + specDelimiter + descriptor.id.value)
         is TestDescriptor ->
            DescriptorPath(render(p, specDelimiter, testDelimiter).value + testDelimiter + descriptor.id.value)
      }
   }

   /**
    * Parses a string representation of a [Descriptor].
    *
    * For format examples see [render].
    */
   fun parse(string: String): Descriptor {
      // we know the spec name has to be included in a descriptor path
      val className = string.substringBefore(SPEC_DELIMITER)
      val specDescriptor = SpecDescriptor(DescriptorId(className.trim()))

      val testsString = string.trim().substringAfter(SPEC_DELIMITER, "")
      if (testsString.isBlank()) return specDescriptor

      val testNames = testsString.split(TEST_DELIMITER)
      return testNames.fold(specDescriptor as Descriptor) { acc, name ->
         acc.append(name.trim().lines().joinToString(" ") { it.trim() })
      }
   }
}

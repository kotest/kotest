package io.kotest.core.descriptors

import io.kotest.common.DescriptorPath
import io.kotest.core.descriptors.Descriptor.SpecDescriptor
import io.kotest.core.descriptors.Descriptor.TestDescriptor
import io.kotest.core.descriptors.DescriptorPaths.render

/**
 * Utility functions for working with [Descriptor] paths.
 */
object DescriptorPaths {

   const val SPEC_DELIMITER = "/"
   const val TEST_DELIMITER = " -- "

   /**
    * Returns a string representation of the given [descriptor] wrapped in a [io.kotest.common.DescriptorPath].
    *
    * Spec descriptors are separated by a slash (/) and test descriptors are separated by a double dash ( -- ).
    *
    * Examples:
    *
    * com.sksamuel.MySpec
    * com.sksamuel.MySpec/a context
    * com.sksamuel.MySpec/a context -- a test
    * com.sksamuel.MySpec/a context -- another context -- another test
    */
   fun render(descriptor: Descriptor): DescriptorPath = when (descriptor) {
      is SpecDescriptor -> DescriptorPath(descriptor.id.value)
      is TestDescriptor -> when (val p = descriptor.parent) {
         is SpecDescriptor -> DescriptorPath(p.id.value + SPEC_DELIMITER + descriptor.id.value)
         is TestDescriptor -> DescriptorPath(render(p).value + TEST_DELIMITER + descriptor.id.value)
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

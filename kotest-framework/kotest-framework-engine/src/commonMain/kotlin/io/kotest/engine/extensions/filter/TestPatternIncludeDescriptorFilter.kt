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
      return descriptor.normalizeLineBreaks().hasSharedPathWithWildcards(tests)
   }

   /**
    * Like [Descriptor.hasSharedPath] but treats `?` in the [pattern] descriptor's test IDs as
    * a single-character wildcard (matching exactly one character in the actual descriptor's IDs).
    *
    * This is needed because `GradleTestFilterBuilder` (in the IntelliJ plugin) replaces `.` in test names with `?` to
    * prevent Gradle from misinterpreting them as FQN separators. When the resulting pattern
    * reaches this filter, we must honour that wildcard so that e.g. the pattern context
    * "1?2?3 my test" still matches a test whose real name is "1.2.3 my test".
    */
   private fun Descriptor.hasSharedPathWithWildcards(pattern: Descriptor): Boolean {
      return wildcardEqual(this, pattern) ||
         wildcardIsAncestorOf(pattern, this) ||
         wildcardIsAncestorOf(this, pattern)
   }

   /** Returns true if [actual] and [pattern] refer to the same node, with `?` wildcards in [pattern]. */
   private fun wildcardEqual(actual: Descriptor, pattern: Descriptor): Boolean = when {
      actual is Descriptor.SpecDescriptor && pattern is Descriptor.SpecDescriptor ->
         actual.isEqual(pattern)
      actual is Descriptor.TestDescriptor && pattern is Descriptor.TestDescriptor ->
         wildcardEqual(actual.parent, pattern.parent) && actual.id.value.matchesGlob(pattern.id.value)
      else -> false
   }

   /**
    * Returns true if [ancestor] is a strict ancestor of [descendant],
    * using wildcard-aware ID comparison.
    */
   private fun wildcardIsAncestorOf(ancestor: Descriptor, descendant: Descriptor): Boolean =
      when (descendant) {
         is Descriptor.SpecDescriptor -> false
         is Descriptor.TestDescriptor ->
            wildcardEqual(ancestor, descendant.parent) || wildcardIsAncestorOf(ancestor, descendant.parent)
      }

   /**
    * Returns true if this string matches [pattern] where `?` in [pattern] stands for
    * exactly one arbitrary character, and all other characters are matched literally.
    */
   private fun String.matchesGlob(pattern: String): Boolean {
      if ('?' !in pattern) return this == pattern
      val regex = pattern.split('?').joinToString(".") { Regex.escape(it) }.toRegex()
      return this.matches(regex)
   }

   private fun Descriptor.normalizeLineBreaks(): Descriptor = when (this) {
      is Descriptor.SpecDescriptor -> this
      is Descriptor.TestDescriptor -> Descriptor.TestDescriptor(
         parent.normalizeLineBreaks(),
         DescriptorId(id.value.removeLineBreaks())
      )
   }

   private fun String.removeLineBreaks(): String = replace(Regex("\r\n|\n|\r"), " ")

   private fun packageMatch(
      pattern: TestPattern,
      descriptor: Descriptor.SpecDescriptor
   ): Boolean {
      val pck = descriptor.id.value.substringBeforeLast(".") // grabs just the package by dropping the class name
      return if (pattern.subpackages) pck.startsWith(pattern.packageName) else pck == pattern.packageName
   }
}

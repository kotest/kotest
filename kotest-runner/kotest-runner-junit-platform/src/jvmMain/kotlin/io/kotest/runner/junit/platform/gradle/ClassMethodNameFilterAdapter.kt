@file:Suppress("KDocUnresolvedReference")

package io.kotest.runner.junit.platform.gradle

import io.kotest.common.env
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.engine.extensions.filter.DescriptorFilter
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.engine.extensions.filter.INCLUDE_PATTERN_ENV
import io.kotest.runner.junit.platform.postFilters
import org.junit.platform.engine.EngineDiscoveryRequest

/**
 * JUnit has this concept of [PostDiscoveryFilter]s which can be applied after test discovery.
 *
 * Gradle implements the cli parameter "--tests Foo.mytest" by passing an instance of
 * [org.gradle.api.internal.tasks.testing.junitplatform.ClassMethodNameFilter] which is an
 * implementation of PostDiscoveryFilter. It is also used by the test retry plugin.
 *
 * This adapter will return a [DescriptorFilter] for each of these filters.
 */
internal object ClassMethodNameFilterAdapter {

   private val logger = Logger<ClassMethodNameFilterAdapter>()

   // Nested test separator: Gradle only understands classes and methods (no nesting), so kotest
   // collapses nested test paths into a fake method name using this delimiter.
   internal const val CONTEXT_SEPARATOR = " -- "

   // Temporary placeholder used when splitting on "." so that "\E.*\Q" wildcard sequences
   // (which contain no ".") are not lost during the split.
   private const val WILDCARD_PLACEHOLDER = "__wildcard__"

   /**
    * Returns a [DescriptorFilter] for each [PostDiscoveryFilter] that is an
    * implementation of [ClassMethodNameFilter].
    *
    * Patterns containing the [CONTEXT_SEPARATOR] token indicate nested tests and are parsed
    * directly into [NestedTestPattern]s; all other patterns are forwarded to
    * [GradleClassMethodRegexTestFilter].
    *
    * If no post-filters are present, this will return an empty list.
    */
   internal fun adapt(request: EngineDiscoveryRequest): List<DescriptorFilter> {

      val patterns = ClassMethodNameFilterUtils.extractIncludePatterns(request.postFilters())
      logger.log { "ClassMethodNameFilter patterns [$patterns]" }
      if (patterns.isEmpty()) {
         return emptyList()
      }

      val nestedPatterns = mutableSetOf<NestedTestPattern>()
      val regexPatterns = mutableSetOf<String>()

      for (pattern in patterns) {
         val nested = parseNestedTestPattern(pattern)
         if (nested != null) {
            nestedPatterns.add(nested)
         } else {
            regexPatterns.add(pattern)
         }
      }

      logger.log { "nestedPatterns [${nestedPatterns.joinToString(", ")}] regexPatterns [${regexPatterns.joinToString(", ")}]" }

      if (nestedPatterns.isNotEmpty()) {
         // HACK since we have a tests filter with a nested test name, we will clear the list of post-filters
         // so Gradle doesn't do any filtering - otherwise, Gradle will incorrectly filter out the nested
         // test as it doesn't understand the kotest format
         ClassMethodNameFilterUtils.reset(request.postFilters())
      }

      val descriptorFilter = when {
         nestedPatterns.isEmpty() -> GradleClassMethodRegexTestFilter(regexPatterns)
         regexPatterns.isEmpty() -> NestedTestDescriptorFilter(nestedPatterns)
         else -> object : DescriptorFilter {
            override fun filter(descriptor: Descriptor): DescriptorFilterResult {
               val descriptorFilters = listOf(
                  GradleClassMethodRegexTestFilter(regexPatterns),
                  NestedTestDescriptorFilter(nestedPatterns),
               )
               return if (descriptorFilters.any { it.filter(descriptor) == DescriptorFilterResult.Include }) {
                  DescriptorFilterResult.Include
               } else {
                  DescriptorFilterResult.Exclude(null)
               }
            }
         }
      }

      return listOf(descriptorFilter)
   }

   /**
    * Parses a Gradle regex pattern into a [NestedTestPattern] if it describes a nested test.
    * Returns `null` for package-only, class-only, or root-test patterns.
    *
    * A pattern is treated as a nested test when the method portion (the segments after the class
    * name) contains at least one [CONTEXT_SEPARATOR] token, giving two or more context levels.
    *
    * Examples that produce a [NestedTestPattern]:
    * - `\Qio.kotest.SomeTest.my test -- nested\E`
    * - `\Qio.kotest.SomeTest.ctx -- level2 -- leaf\E`
    * - `\Qio.kotest.SomeTest.test -- with a \E.*\Q wildcard\E`
    *
    * Examples that return `null`:
    * - `\Qio.kotest\E` (package only)
    * - `\Qio.kotest.SomeTest\E` (class only)
    * - `\Qio.kotest.SomeTest.my test\E` (root test — no separator)
    * - `.*\QFooTest\E` (leading wildcard — class matching handled by [GradleClassMethodRegexTestFilter])
    */
   internal fun parseNestedTestPattern(pattern: String): NestedTestPattern? {
      require(pattern.isNotBlank())
      val parts = splitPattern(pattern)

      // Leading lowercase parts are the package; the first uppercase-starting part is the class.
      val classIndex = parts.indexOfFirst { it.isNotEmpty() && it.first().isUpperCase() }
      if (classIndex == -1) return null

      val packageParts = parts.take(classIndex)
      val className = parts[classIndex]
      val methodPart = parts.drop(classIndex + 1).joinToString(".")

      val contexts = methodPart.split(CONTEXT_SEPARATOR).filter { it.isNotBlank() }

      // We only handle nested tests (2+ context segments). Root tests go to GradleClassMethodRegexTestFilter.
      if (contexts.size < 2) return null

      val fqcn = if (packageParts.isEmpty()) className
      else "${packageParts.joinToString(".")}.$className"
      return NestedTestPattern(fqcn, contexts)
   }

   /**
    * Splits a Gradle regex pattern into its dot-separated components, preserving wildcard
    * sequences that would otherwise be lost across the split boundary.
    *
    * The Gradle regex encoding is:
    * - Literal text is wrapped in `\Q...\E` blocks.
    * - The `*` wildcard is represented as `\E.*\Q` (close literal, any-char, open literal).
    *
    * We temporarily replace `\E.*\Q` with [WILDCARD_PLACEHOLDER] so the split on `"."` does
    * not cut through a wildcard, then restore `*` afterwards.
    */
   private fun splitPattern(pattern: String): List<String> {
      return pattern
         .removePrefix("\\Q")
         .removeSuffix("\\E")
         .replace("\\E.*\\Q", WILDCARD_PLACEHOLDER)
         .split(".")
         .filter { it.isNotEmpty() }
         .map { it.replace(WILDCARD_PLACEHOLDER, "*") }
   }
}

/**
 * Represents a parsed nested-test filter pattern.
 *
 * @param fqcn fully qualified spec class name (e.g. `"io.kotest.SomeTest"`)
 * @param contexts the ordered path from root context to target, containing at least 2 elements
 */
internal data class NestedTestPattern(val fqcn: String, val contexts: List<String>) {
   init {
      require(contexts.size >= 2) { "nested test patterns must have at least 2 context segments, got: $contexts" }
   }
}

/**
 * A [DescriptorFilter] that matches descriptors against a set of [NestedTestPattern]s.
 *
 * A descriptor is included when it shares a path with the target descriptor built from the
 * pattern — i.e. the descriptor is the target itself, an ancestor of it, or a descendant of it.
 * This ensures the spec, all intermediate containers, and leaf tests under the target are all
 * included when a nested test filter is active.
 */
internal class NestedTestDescriptorFilter(private val patterns: Set<NestedTestPattern>) : DescriptorFilter {

   private val logger = Logger(NestedTestDescriptorFilter::class)

   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      logger.log { Pair(descriptor.toString(), "Testing against nested test patterns $patterns") }
      val env = env(INCLUDE_PATTERN_ENV)
      return when {
         env != null -> DescriptorFilterResult.Include
         patterns.isEmpty() -> DescriptorFilterResult.Include
         patterns.any { match(it, descriptor) } -> DescriptorFilterResult.Include
         else -> DescriptorFilterResult.Exclude(null)
      }
   }

   private fun match(pattern: NestedTestPattern, descriptor: Descriptor): Boolean {
      val spec = Descriptor.SpecDescriptor(DescriptorId(pattern.fqcn))
      val target: Descriptor = pattern.contexts.fold(spec) { acc, ctx -> acc.append(ctx) }
      return descriptor.hasSharedPath(target)
   }
}

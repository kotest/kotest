@file:Suppress("KDocUnresolvedReference")

package io.kotest.runner.junit.platform.gradle

import io.kotest.core.Logger
import org.junit.platform.launcher.PostDiscoveryFilter
import java.util.regex.Pattern

/**
 * JUnit has this concept of 'PostDiscoveryFilter's which can be applied after test discovery.
 *
 * Gradle implements the cli parameter "--tests Foo.mytest" by passing an instance of
 * [org.gradle.api.internal.tasks.testing.junitplatform.ClassMethodNameFilter] which is an
 * implementation of PostDiscoveryFilter. It is also used by the test retry plugin.
 *
 * But ClassMethodNameFilter, as the name implies, only handles classes and methods.
 * Kotest is more advanced, and JUnit5 Platform allows for hierarchical tests, so this is a limitation
 * of Gradle not implementing the spec fully. See https://github.com/gradle/gradle/issues/4912
 *
 * Since ClassMethodNameFilter is private, we can't get access to the underlying patterns, so we resort
 * to this reflection hackery to get the raw strings out, so we can parse and apply the patterns ourselves,
 * thus allowing kotest to properly support the --tests options for nested tests.
 *
 * Note: There are two Gradle version-dependent changes we handle:
 *
 * 1. In Gradle 9.4+, `ClassMethodNameFilter` is no longer passed directly as a `PostDiscoveryFilter`.
 *    Instead, it is wrapped inside a `DelegatingByTypeFilter` which dispatches to different filters
 *    based on `TestSource` type. We detect this wrapper and extract `ClassMethodNameFilter` from it.
 *
 * 2. In Gradle 9.4+, `TestSelectionMatcher` was refactored. The `commandLineIncludePatterns` and
 *    `buildScriptIncludePatterns` fields moved from `TestSelectionMatcher` into a new delegate class
 *    `ClassTestSelectionMatcher`, accessed via the `classTestSelectionMatcher` field.
 *
 */
internal object ClassMethodNameFilterUtils {

   private val logger = Logger(ClassMethodNameFilterUtils::class)

   /**
    * Returns the include patterns enclosed in any [ClassMethodNameFilter]s added by Gradle
    * from the --tests command line arg.
    *
    * In Gradle < 9.4, [ClassMethodNameFilter] appears directly in the post-discovery filters list.
    * In Gradle >= 9.4, it is wrapped inside a [DelegatingByTypeFilter], so we unwrap it first.
    */
   fun extractIncludePatterns(filters: List<Any>): List<String> {
      val classMethodFilters = resolveClassMethodNameFilters(filters)
      return classMethodFilters.flatMap { extract(it) }
   }

   private fun extract(filter: Any): List<String> = runCatching {

      val matcher = testMatcher(filter)
      logger.log { "TestMatcher [$matcher]" }

      // Resolve the object that holds the include pattern lists.
      // In Gradle < 9.4 this is the TestSelectionMatcher itself.
      // In Gradle >= 9.4 the fields moved to ClassTestSelectionMatcher,
      // accessed via TestSelectionMatcher.classTestSelectionMatcher.
      val patternHolder = resolvePatternHolder(matcher)
      logger.log { "PatternHolder [${patternHolder::class.java.name}]" }

      val buildScriptIncludePatterns = buildScriptIncludePatterns(patternHolder)
      logger.log { "buildScriptIncludePatterns [$buildScriptIncludePatterns]" }

      val commandLineIncludePatterns = commandLineIncludePatterns(patternHolder)
      logger.log { "commandLineIncludePatterns [$commandLineIncludePatterns]" }

      val regexes = buildList {
         addAll(buildScriptIncludePatterns)
         addAll(commandLineIncludePatterns)
      }.map { pattern(it) }

      logger.log { "ClassMethodNameFilter regexes [$regexes]" }
      regexes
   }.getOrElse { e ->
      logger.log { "Failed to extract include patterns from ClassMethodNameFilter via reflection: $e" }
      emptyList()
   }

   /**
    * Removes any include patterns on any [ClassMethodNameFilter]s added by Gradle.
    *
    * Both `commandLineIncludePatterns` (populated by `--tests`) and
    * `buildScriptIncludePatterns` (populated by `tasks.test { filter { includeTestsMatching(...) } }`)
    * are cleared, since [extractIncludePatterns] reads from both.
    */
   fun reset(filters: List<Any>) {
      resolveClassMethodNameFilters(filters).forEach {
         runCatching {
            val matcher = testMatcher(it)
            val patternHolder = resolvePatternHolder(matcher)
            (commandLineIncludePatterns(patternHolder) as MutableList<*>).clear()
            (buildScriptIncludePatterns(patternHolder) as MutableList<*>).clear()
         }.onFailure { e ->
            logger.log { "Failed to reset ClassMethodNameFilter via reflection: $e" }
         }
      }
   }

   /**
    * Finds all `ClassMethodNameFilter` instances from the post-discovery filters list.
    *
    * In Gradle < 9.4, `ClassMethodNameFilter` appears directly in the list.
    * In Gradle >= 9.4, `ClassMethodNameFilter` is wrapped inside a `DelegatingByTypeFilter`
    * which has a `delegates` map of `TestSource` type → `PostDiscoveryFilter`. We extract
    * the `ClassMethodNameFilter` instances from those delegate maps.
    */
   private fun resolveClassMethodNameFilters(filters: List<Any>): List<Any> {
      val result = mutableListOf<Any>()

      for (filter in filters) {
         when (filter.javaClass.simpleName) {
            "ClassMethodNameFilter" -> result.add(filter)
            "DelegatingByTypeFilter" -> {
               // Gradle >= 9.4: unwrap ClassMethodNameFilter from the DelegatingByTypeFilter's delegates map
               runCatching {
                  val delegatesField = filter::class.java.getDeclaredField("delegates")
                  delegatesField.isAccessible = true
                  val delegates = delegatesField.get(filter) as? Map<*, *> ?: emptyMap<Any, Any>()
                  for ((_, delegate) in delegates) {
                     if (delegate != null && delegate.javaClass.simpleName == "ClassMethodNameFilter") {
                        result.add(delegate)
                     }
                  }
               }.onFailure { e ->
                  logger.log { "Failed to unwrap DelegatingByTypeFilter via reflection: $e" }
               }
            }
         }
      }

      // Deduplicate: in Gradle 9.4, the same ClassMethodNameFilter instance is registered
      // as a delegate for both ClassSource and MethodSource types
      return result.distinct()
   }

   private fun testMatcher(obj: Any): Any {
      val field = obj::class.java.getDeclaredField("matcher")
      field.isAccessible = true
      return field.get(obj)
   }

   /**
    * Resolves the object that holds `commandLineIncludePatterns` and `buildScriptIncludePatterns`.
    *
    * In Gradle < 9.4, these fields live directly on [TestSelectionMatcher].
    * In Gradle >= 9.4, [TestSelectionMatcher] was refactored and the fields moved to a
    * delegate class [ClassTestSelectionMatcher], accessed via the `classTestSelectionMatcher` field.
    *
    * We try the old layout first (direct field access) and fall back to the new delegate layout.
    */
   private fun resolvePatternHolder(matcher: Any): Any {
      // Try the old layout: commandLineIncludePatterns directly on the matcher (Gradle < 9.4)
      return runCatching {
         matcher::class.java.getDeclaredField("commandLineIncludePatterns")
         matcher // field exists directly — use the matcher itself
      }.getOrElse {
         // New layout (Gradle >= 9.4): delegate through classTestSelectionMatcher
         val field = matcher::class.java.getDeclaredField("classTestSelectionMatcher")
         field.isAccessible = true
         field.get(matcher)
      }
   }

   private fun commandLineIncludePatterns(obj: Any): List<Any> {
      val field = obj::class.java.getDeclaredField("commandLineIncludePatterns")
      field.isAccessible = true
      @Suppress("UNCHECKED_CAST")
      return field.get(obj) as List<Any>
   }

   private fun buildScriptIncludePatterns(obj: Any): List<Any> {
      val field = obj::class.java.getDeclaredField("buildScriptIncludePatterns")
      field.isAccessible = true
      @Suppress("UNCHECKED_CAST")
      return field.get(obj) as List<Any>
   }

   private fun pattern(obj: Any): String {
      val field = obj::class.java.getDeclaredField("pattern")
      field.isAccessible = true
      val pattern: Pattern = field.get(obj) as Pattern
      return pattern.pattern()
   }
}


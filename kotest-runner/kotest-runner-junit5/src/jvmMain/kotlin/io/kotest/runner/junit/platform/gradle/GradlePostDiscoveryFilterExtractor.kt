package io.kotest.runner.junit.platform.gradle

import io.kotest.core.Logger
import java.util.regex.Pattern

/**
 * JUnit has this concept of 'PostDiscoveryFilter's which can be applied after test discovery.
 * Gradle implements --tests Foo.mytest by passing a ClassMethodNameFilter which is an implementation
 * of PostDiscoveryFilter. It is also used by the test retry plugin.
 *
 * But ClassMethodNameFilter, as the name implies, only handles clases and methods.
 * Kotest is more advanced, and JUnit5 Platform allows for hierarchical tests, so this is a limitation
 * of Gradle not implementing the spec fully. See https://github.com/gradle/gradle/issues/4912
 *
 * Since ClassMethodNameFilter is private, we can't get access to the underlying patterns, so we resort
 * to this reflection bullshit to get the raw strings out, so we can parse and apply the patterns ourselves,
 * thus allowing kotest to properly support the --tests options.
 *
 */
internal object GradlePostDiscoveryFilterExtractor {

   private val logger = Logger(GradlePostDiscoveryFilterExtractor::class)

   fun extract(filters: List<Any>): List<String> {
      val classMethodFilters = filters.filter { it.javaClass.simpleName == "ClassMethodNameFilter" }
      return classMethodFilters.flatMap { extract(it) }
   }

   private fun extract(filter: Any): List<String> = runCatching {

      val matcher = testMatcher(filter)
      logger.log { Pair(null, "TestMatcher [$matcher]") }

      val buildScriptIncludePatterns = buildScriptIncludePatterns(matcher)
      logger.log { Pair(null, "buildScriptIncludePatterns [$buildScriptIncludePatterns]") }

      val commandLineIncludePatterns = commandLineIncludePatterns(matcher)
      logger.log { Pair(null, "commandLineIncludePatterns [$commandLineIncludePatterns]") }

      val regexes = buildList {
         addAll(buildScriptIncludePatterns)
         addAll(commandLineIncludePatterns)
      }.map { pattern(it) }

      logger.log { Pair(null, "ClassMethodNameFilter regexes [$regexes]") }
      regexes
   }.getOrElse { emptyList() }

   private fun testMatcher(obj: Any): Any {
      val field = obj::class.java.getDeclaredField("matcher")
      field.isAccessible = true
      return field.get(obj)
   }

   private fun commandLineIncludePatterns(obj: Any): List<Any> {
      val field = obj::class.java.getDeclaredField("commandLineIncludePatterns")
      field.isAccessible = true
      return field.get(obj) as List<Any>
   }

   private fun buildScriptIncludePatterns(obj: Any): List<Any> {
      val field = obj::class.java.getDeclaredField("buildScriptIncludePatterns")
      field.isAccessible = true
      return field.get(obj) as List<Any>
   }

   private fun pattern(obj: Any): String {
      val field = obj::class.java.getDeclaredField("pattern")
      field.isAccessible = true
      val pattern: Pattern = field.get(obj) as Pattern
      return pattern.pattern()
   }
}


package io.kotest.engine.spec.interceptor

import io.kotest.common.KotestInternal
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.mpp.bestName
import io.kotest.mpp.env
import io.kotest.mpp.log
import io.kotest.mpp.sysprop
import kotlin.reflect.KClass

/**
 * Applies test and spec filters using sysprop or env vars from [KotestEngineProperties.filterTests]
 * and [KotestEngineProperties.filterSpecs].
 *
 * These work similarly to gradle filters in --tests described at:
 * https://docs.gradle.org/current/userguide/java_testing.html#full_qualified_name_pattern
 */
@OptIn(KotestInternal::class)
internal class SystemPropertySpecFilterInterceptor(
   private val listener: TestEngineListener,
   private val registry: ExtensionRegistry
) : SpecRefInterceptor {

   private fun syspropOrEnv(name: String) = sysprop(name) ?: env(name) ?: ""

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->

      val included = syspropOrEnv(KotestEngineProperties.filterSpecs)
         .propertyToRegexes()
         .map { it.toSpecFilter() }
         .all { it.filter(ref.kclass) == SpecFilterResult.Include }

      log { "SystemPropertySpecFilterInterceptor: ${ref.kclass} included = $included" }

      if (included) {
         fn(ref)
      } else {
         listener.specIgnored(ref.kclass)
         SpecExtensions(registry).ignored(ref.kclass)
         emptyMap()
      }
   }
}

private fun Regex.toSpecFilter(): SpecFilter = object : SpecFilter {
   override fun filter(kclass: KClass<*>): SpecFilterResult {
      val name = kclass.bestName()
      return if (this@toSpecFilter.matches(name)) SpecFilterResult.Include else SpecFilterResult.Exclude
   }
}

private fun String.propertyToRegexes(): List<Regex> =
   this.split(",")
      .filter { it.isNotBlank() }
      .map { it.replace("*", ".*?").toRegex() }

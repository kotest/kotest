package io.kotest.engine.spec.interceptor.ref

import io.kotest.engine.flatMap
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.core.Logger
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.mpp.bestName
import io.kotest.mpp.syspropOrEnv
import kotlin.reflect.KClass

/**
 * Applies spec filters using sysprop or env vars from [KotestEngineProperties.filterSpecs].
 *
 * These work similarly to gradle filters in --tests described at:
 * https://docs.gradle.org/current/userguide/java_testing.html#full_qualified_name_pattern
 */
internal class SystemPropertySpecFilterInterceptor(
   private val listener: TestEngineListener,
   registry: ExtensionRegistry
) : SpecRefInterceptor {

   private val logger = Logger(SystemPropertySpecFilterInterceptor::class)
   private val extensions = SpecExtensions(registry)

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      val filter = syspropOrEnv(KotestEngineProperties.filterSpecs) ?: ""
      logger.log { Pair(ref.kclass.bestName(), "Filter specs syspropOrEnv=$filter") }

      val included = filter
         .propertyToRegexes()
         .map { it.toSpecFilter() }
         .all { it.filter(ref.kclass) == SpecFilterResult.Include }

      logger.log { Pair(ref.kclass.bestName(), "included = $included") }

      return if (included) {
         next.invoke(ref)
      } else {
         runCatching {
            listener.specIgnored(
               ref.kclass,
               "Filtered by ${KotestEngineProperties.filterSpecs} spec filter"
            )
         }.flatMap { extensions.ignored(ref.kclass, "Filtered by ${KotestEngineProperties.filterSpecs} spec filter") }
            .map { emptyMap() }
      }
   }
}

private fun Regex.toSpecFilter(): SpecFilter = object : SpecFilter {
   override fun filter(kclass: KClass<*>): SpecFilterResult {
      val name = kclass.bestName()
      return if (this@toSpecFilter.matches(name)) SpecFilterResult.Include else SpecFilterResult.Exclude("Disabled by spec filter: $this")
   }
}

private fun String.propertyToRegexes(): List<Regex> =
   this.split(",")
      .filter { it.isNotBlank() }
      .map { it.replace("*", ".*?").toRegex() }

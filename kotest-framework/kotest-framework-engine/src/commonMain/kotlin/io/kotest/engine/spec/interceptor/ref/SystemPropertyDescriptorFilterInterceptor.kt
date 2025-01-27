//package io.kotest.engine.spec.interceptor.ref
//
//import io.kotest.core.Logger
//import io.kotest.core.descriptors.Descriptor
//import io.kotest.core.spec.SpecRef
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestResult
//import io.kotest.engine.config.KotestEngineProperties
//import io.kotest.engine.descriptors.toDescriptor
//import io.kotest.engine.extensions.DescriptorFilter
//import io.kotest.engine.extensions.DescriptorFilterResult
//import io.kotest.engine.flatMap
//import io.kotest.engine.listener.TestEngineListener
//import io.kotest.engine.spec.SpecExtensions
//import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
//import io.kotest.engine.spec.interceptor.SpecRefInterceptor
//import io.kotest.mpp.bestName
//import io.kotest.mpp.syspropOrEnv
//import kotlin.reflect.KClass
//
///**
// * Applies descriptor filters using sysprop or env vars from [KotestEngineProperties.filterSpecs].
// *
// * These work similarly to gradle filters in --tests described at:
// * https://docs.gradle.org/current/userguide/java_testing.html#full_qualified_name_pattern
// */
//internal class SystemPropertyDescriptorFilterInterceptor(
//   private val listener: TestEngineListener,
//   private val specExtensions: SpecExtensions,
//) : SpecRefInterceptor {
//
//   private val logger = Logger(SystemPropertyDescriptorFilterInterceptor::class)
//
//   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
//      val filter = syspropOrEnv(KotestEngineProperties.filterSpecs) ?: ""
//      logger.log { Pair(ref.kclass.bestName(), "Filter specs syspropOrEnv=$filter") }
//
//      val included = filter
//         .propertyToRegexes()
//         .map { it.toSpecFilter() }
//         .all { it.filter(ref.kclass.toDescriptor()) == DescriptorFilterResult.Include }
//
//      logger.log { Pair(ref.kclass.bestName(), "included = $included") }
//
//      return if (included) {
//         next.invoke(ref)
//      } else {
//         runCatching {
//            listener.specIgnored(
//               ref.kclass,
//               "Filtered by ${KotestEngineProperties.filterSpecs} spec filter"
//            )
//         }.flatMap { specExtensions.ignored(ref.kclass, "Filtered by ${KotestEngineProperties.filterSpecs} spec filter") }
//            .map { emptyMap() }
//      }
//   }
//}
//
//private fun Regex.toSpecFilter(): DescriptorFilter = object : DescriptorFilter {
//   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
//      val name = kclass.bestName()
//      return if (this@toSpecFilter.matches(name)) DescriptorFilterResult.Include else DescriptorFilterResult.Exclude("Disabled by spec filter: $this")
//   }
//}
//
//private fun String.propertyToRegexes(): List<Regex> =
//   this.split(",")
//      .filter { it.isNotBlank() }
//      .map { it.replace("*", ".*?").toRegex() }

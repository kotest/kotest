package io.kotest.engine.spec.interceptor

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.SpecRefExtension
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.mpp.log

/**
 * A [SpecRefInterceptor] that will invoke any [SpecRefExtension]s.
 */
class SpecRefExtensionInterceptor(private val registry: ExtensionRegistry) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRefContainer,
      fn: suspend (SpecRefContainer) -> Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>>
   ): Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>> {

      log { "SpecReferenceExtensionInterceptor: Intercepting spec" }

      val exts = registry.all().filterIsInstance<SpecRefExtension>()
      var results: Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>> = Result.success(ref to emptyMap())
      val inner: suspend (SpecRef) -> Unit = { results = fn(ref) }

      val chain = exts.foldRight(inner) { op, acc -> { op.interceptRef(ref.specRef) { acc(ref.specRef) } } }
      chain.invoke(ref.specRef)

      return results
   }
}

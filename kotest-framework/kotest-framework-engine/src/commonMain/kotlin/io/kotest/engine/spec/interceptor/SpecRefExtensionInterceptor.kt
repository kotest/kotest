package io.kotest.engine.spec.interceptor

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.SpecRefExtension
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.mpp.log

/**
 * A [SpecRefInterceptor] that will invoke any [SpecRefExtension]s.
 */
class SpecRefExtensionInterceptor(private val conf: Configuration) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->

      log { "SpecReferenceExtensionInterceptor: Intercepting spec" }

      val exts = conf.extensions().filterIsInstance<SpecRefExtension>()
      var results: Map<TestCase, TestResult> = emptyMap()
      val inner: suspend (SpecRef) -> Unit = { results = fn(it) }

      val chain = exts.foldRight(inner) { op, acc -> { op.interceptRef(ref) { acc(ref) } } }
      chain.invoke(ref)

      results
   }
}

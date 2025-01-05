package io.kotest.engine.spec.interceptor.ref

import io.kotest.engine.config.ExtensionRegistry
import io.kotest.core.extensions.SpecRefExtension
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.core.Logger
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.mpp.bestName

/**
 * A [SpecRefInterceptor] that will invoke any [SpecRefExtension]s.
 */
internal class SpecRefExtensionInterceptor(private val registry: ExtensionRegistry) : SpecRefInterceptor {

   private val logger = Logger(SpecRefExtensionInterceptor::class)

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {

      logger.log { Pair(ref.kclass.bestName(), "Intercepting spec") }

      val exts = registry.all().filterIsInstance<SpecRefExtension>()
      var results: Result<Map<TestCase, TestResult>> = Result.success(emptyMap())
      val inner: suspend (SpecRef) -> Unit = { results = next.invoke(ref) }

      val chain = exts.foldRight(inner) { op, acc -> { op.interceptRef(ref) { acc(ref) } } }
      chain.invoke(ref)

      return results
   }
}

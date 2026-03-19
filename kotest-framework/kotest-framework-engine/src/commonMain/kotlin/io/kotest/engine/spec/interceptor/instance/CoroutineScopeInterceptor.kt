package io.kotest.engine.spec.interceptor.instance

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

/**
 * Configures a [CoroutineScope] provided by a [Spec] for launching spec level coroutines.
 */
@OptIn(ExperimentalKotest::class)
internal object CoroutineScopeInterceptor : SpecInterceptor {

   override suspend fun intercept(
      spec: Spec,
      ref: SpecRef,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {
      return coroutineScope {
         spec.scope = this
         next.invoke(spec)
      }
   }
}

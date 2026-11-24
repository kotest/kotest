package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.errors.ExtensionExceptionExtractor
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.engine.test.TestResult

/**
 * Invokes any [BeforeSpecListener] and [AfterSpecListener] callbacks for the given spec.
 *
 * Any error in a user callback causes the spec to be aborted. The [AfterSpecListener]s are
 * still invoked when a [BeforeSpecListener] failed, so that resources acquired by the
 * callbacks that did run are released. If both fail, all errors are reported.
 */
internal class BeforeAfterSpecCallbacksInterceptor(
   private val specExtensions: SpecExtensions,
) : SpecInterceptor {

   override suspend fun intercept(
      spec: Spec,
      ref: SpecRef,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {

      // any errors in user callbacks cause us to abort the spec
      val result = runCatching {
         specExtensions.beforeSpec(spec)
         next.invoke(spec)
      }

      // the after callbacks must run even if the before callbacks failed, so that
      // resources they acquired are released
      val afterResult = specExtensions.afterSpec(spec)

      val errors = listOfNotNull(result.exceptionOrNull(), afterResult.exceptionOrNull())
         .flatMap { ExtensionExceptionExtractor.flatten(it) }

      return when (errors.size) {
         0 -> result.getOrThrow()
         1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }
}

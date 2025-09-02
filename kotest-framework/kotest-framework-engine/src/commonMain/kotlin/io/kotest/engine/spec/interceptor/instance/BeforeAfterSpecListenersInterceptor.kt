package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.flatMap
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.status.isEnabled

/**
 * Invokes any [io.kotest.core.listeners.BeforeSpecListener] callbacks by delegating to [io.kotest.engine.spec.SpecExtensions], if this is the first test that has
 * executed for this instance of the spec. If any callback fails, further tests are skipped and marked as ignored.
 *
 * This spec level callback is executed at the test stage, because we only want to invoke it if
 * there is at least one enabled test. And since tests can be disabled or enabled programatically,
 * we must defer execution until after the test blocks have been registered (if any).
 */
internal class BeforeAfterSpecListenersInterceptor(
   private val context: EngineContext,
) : SpecInterceptor {

   private val specExtensions = SpecExtensions(context.specConfigResolver, context.projectConfigResolver)
   private val materializer = Materializer(context.specConfigResolver)

   override suspend fun intercept(spec: Spec, next: NextSpecInterceptor): Result<Map<TestCase, TestResult>> {

      val rootTests = materializer.materialize(spec)

      // we only run if we have at least one enabled root test
      val anyEnabled = rootTests.any {
         it.isEnabled(context.projectConfigResolver, context.specConfigResolver, context.testConfigResolver).isEnabled
      }

      return if (anyEnabled) {
         specExtensions.beforeSpec(spec).flatMap {
            next.invoke(spec).flatMap { results ->
               specExtensions
                  .afterSpec(spec)
                  .map { results }
            }
         }
      } else {
         Result.success(emptyMap())
      }
   }
}

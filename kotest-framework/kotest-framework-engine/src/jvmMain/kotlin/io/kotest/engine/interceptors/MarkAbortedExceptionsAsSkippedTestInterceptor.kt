package io.kotest.engine.interceptors

import io.kotest.common.JVMOnly
import io.kotest.common.KotestInternal
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.interceptor.SpecInterceptor
import org.opentest4j.TestAbortedException

/**
 * Writes failed specs to a file so that the [io.kotest.engine.spec.FailureFirstSorter]
 * can use the file to run failed specs first.
 *
 * Note: This is a JVM only feature.
 */
@JVMOnly
internal object MarkAbortedExceptionsAsSkippedTestInterceptor : SpecInterceptor {

   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return fn(spec).map { success ->
         success.mapValues { (_, result) ->
            if (result.errorOrNull is TestAbortedException) {
               TestResult.Ignored
            } else {
               result
            }
         }
      }
   }
}

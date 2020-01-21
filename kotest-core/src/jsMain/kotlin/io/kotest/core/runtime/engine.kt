package io.kotest.core.runtime

import io.kotest.core.spec.Spec
import io.kotest.core.spec.materializeRootTests
import io.kotest.fp.Try

actual class JsTestEngine {

   private fun runTests(spec: Spec) = Try {
      spec.materializeRootTests().forEach { TestExecutor().execute(it.testCase) }
   }

   actual fun execute(spec: Spec) {
      beforeAll()
         .flatMap { runTests(spec) }
         .fold(
            {
               afterAll()
               end(it)
            },
            {
               afterAll().fold(
                  { t -> end(t) },
                  { end(null) }
               )
            }
         )
   }

   private fun end(t: Throwable?) {
      if (t != null) throw t
   }
}

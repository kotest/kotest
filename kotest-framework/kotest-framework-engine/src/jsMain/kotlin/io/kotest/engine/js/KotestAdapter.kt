package io.kotest.engine.js

import io.kotest.engine.spec.AbstractSpec
import kotlin.test.FrameworkAdapter

/**
 * Kotest [FrameworkAdapter] for kotlin-js test support.
 */
object KotestAdapter : FrameworkAdapter {

   override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
      describe(name, suiteFn)
   }

   override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
      // if the test name is the special marker method, we just invoke the function rather
      // than delegating to the test framework. This is to avoid the marker method appearing in
      // test output.
      if (name == AbstractSpec::javascriptTestInterceptor.name) {
         testFn()
      }
   }
}

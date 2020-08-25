package io.kotest.core.js

import io.kotest.core.spec.Spec
import kotlin.test.FrameworkAdapter

/**
 * Kotest [FrameworkAdapter] for kotlin-js test support.
 */
internal object KotestFrameworkAdapter : FrameworkAdapter {

   override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
      describe(name, suiteFn)
   }

   override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
      // if the test name is the special marker method, we just invoke the function rather
      // than delegating to the test framework. This is to avoid the marker method appearing in
      // test output.
      if (name == Spec::kotestJavascript.name) {
         val spec = testFn() as Spec
         executeSpec(spec)
      }
   }
}

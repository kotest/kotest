package io.kotest.core.runtime

import io.kotest.core.spec.SpecConfiguration
import kotlin.test.FrameworkAdapter

object KotestAdapter : FrameworkAdapter {

   override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
      describe(name, suiteFn)
   }

   override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
      // if the test name is the special marker method, we just invoke the function rather
      // than delegating to the test framework. This is to avoid the marker method appearing in
      // test output. For other tests, they will be passed through to "it"
      if (name == SpecConfiguration::javascriptTestInterceptor.name) {
         println("foo")
         testFn()
      } else {
         it(name, testFn)
      }
   }
}

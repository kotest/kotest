package io.kotest.engine.js

internal actual val kotlinJsTestFramework: KotlinJsTestFramework = object : KotlinJsTestFramework {
   override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
      frameworkAdapter.suite(name, ignored, suiteFn)
   }

   override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
      frameworkAdapter.test(name, ignored, testFn)
   }
}

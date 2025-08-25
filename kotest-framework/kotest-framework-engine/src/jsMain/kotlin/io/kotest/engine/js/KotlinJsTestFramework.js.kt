package io.kotest.engine.js

/**
 * An implementation of [KotlinJsTestFramework] for Kotlin/JS that in turn delegates to a
 * [FrameworkAdapter] provided by the kotlin-js-test-framework.
 */
internal actual val kotlinJsTestFramework: KotlinJsTestFramework = object : KotlinJsTestFramework {
   override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
      frameworkAdapter.suite(name, ignored, suiteFn)
   }

   override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
      frameworkAdapter.test(name, ignored, testFn)
   }
}

package io.kotest.engine.js

/**
 * JS [FrameworkAdapter] interface defined by the Kotlin/JS test infra.
 *
 * This is an interface implemented by the Kotlin test framework and acts as an adapter over
 * framework specific test functions at runtime.
 *
 * In theory a js framework called wibble-test might have a function called runSuite and another
 * framework called wobble-test might have a function called suiteMe. This interface allows the kotlin
 * compiler to generate code that can work with either framework at runtime as configured in gradle.
 *
 * This interface allows framework function invocations to be conditionally transformed as required for proper
 * reporting of [failing JS tests on Node.js](https://youtrack.jetbrains.com/issue/KT-64533).
 *
 * Note: Inside the Kotlin/JS test infra, the interface is actually known as `KotlinTestRunner`:
 * https://github.com/JetBrains/kotlin/blob/v1.9.23/libraries/tools/kotlin-test-js-runner/src/KotlinTestRunner.ts
 *
 * Note: Proper test reporting depends on using kotlinTest.adapterTransformer, which is defined here for Node.js:
 * https://github.com/JetBrains/kotlin/blob/v1.9.23/libraries/tools/kotlin-test-js-runner/nodejs.ts
 */
internal external interface FrameworkAdapter {
   /** Declares a test suite. */
   fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit)

   /** Declares a test. */
   fun test(name: String, ignored: Boolean, testFn: () -> Any?)
}

// Conditional transformation required by the Kotlin/JS test infra.
internal val frameworkAdapter: FrameworkAdapter by lazy {
   val originalAdapter = JasmineLikeAdapter()
   if (jsTypeOf(kotlinTestNamespace) != "undefined") {
      kotlinTestNamespace.adapterTransformer?.invoke(originalAdapter) ?: originalAdapter
   } else {
      originalAdapter
   }
}

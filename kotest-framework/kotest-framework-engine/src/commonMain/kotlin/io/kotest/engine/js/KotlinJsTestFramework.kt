package io.kotest.engine.js

/**
 * The entry point that the Kotest Test Engine can invoke to run tests on javascript platforms.
 *
 * Since js and wasmJs differ slightly in how they operate at runtime, the test engine invokes
 * the suite and test methods in this interface. Then at runtime, the appropriate implementation
 * of this handles the nuances of the underlying platform.
 *
 * Nesting of test suites may not be supported by TeamCity reporters of kotlin-test-js-runner.
 * @see https://github.com/JetBrains/kotlin/tree/v1.9.23/libraries/tools/kotlin-test-js-runner
 */
internal interface KotlinJsTestFramework {

   /**
    * Declares a test suite. (Theoretically, suites may be nested and may contain tests at each level.)
    *
    * [suiteFn] declares one or more tests (and/or sub-suites, theoretically).
    * Due to [limitations of JS test frameworks](https://github.com/mochajs/mocha/issues/2975) supported by
    * Kotlin's test infra, [suiteFn] cannot handle asynchronous invocations.
    */
   fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit)

   /**
    * Declares a test.
    *
    * [testFn] may return a `Promise`-like object for asynchronous invocation. Otherwise, the underlying JS test
    * framework will invoke [testFn] synchronously.
    */
   fun test(name: String, ignored: Boolean, testFn: () -> Any?)
}

internal expect val kotlinJsTestFramework: KotlinJsTestFramework

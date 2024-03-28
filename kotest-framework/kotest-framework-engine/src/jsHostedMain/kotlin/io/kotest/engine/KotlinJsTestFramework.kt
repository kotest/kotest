package io.kotest.engine

import kotlinx.coroutines.CoroutineScope

/**
 * A view of the test infrastructure API provided by the Kotlin Gradle plugins.
 *
 * API description:
 * - https://github.com/JetBrains/kotlin/blob/v1.9.23/libraries/kotlin.test/js/src/main/kotlin/kotlin/test/TestApi.kt#L38
 * NOTE: This API does not require `kotlin.test` as a dependency. It is actually provided by
 * - https://github.com/JetBrains/kotlin/tree/v1.9.23/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/targets/js/testing/mocha/KotlinMocha.kt
 * - https://github.com/JetBrains/kotlin/tree/v1.9.23/libraries/tools/kotlin-test-js-runner
 *
 * Nesting of test suites may not be supported by TeamCity reporters of kotlin-test-js-runner.
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

/**
 * Returns an invocation of [testFunction] as a Promise.
 *
 * As there is no common `Promise` type in kotlinx-coroutines, implementations return a target-specific type.
 * The Promise is passed to the type-agnostic JS test framework, which discovers a Promise-like object by
 * [probing for a `then` method](https://jasmine.github.io/tutorials/async).
 */
internal expect fun CoroutineScope.testFunctionPromise(testFunction: suspend () -> Unit): Any?

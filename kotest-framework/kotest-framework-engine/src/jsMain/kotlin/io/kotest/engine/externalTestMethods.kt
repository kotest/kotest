package io.kotest.engine

///**
// * Defines the jasmine/mocha/karma style test function names as external functions.
// * Then we can invoke them from the test engine.
// *
// * Note: At runtime, one of the supported JS test frameworks must make these functions available.
// */
//external fun describe(name: String, fn: () -> Unit)
//external fun xdescribe(name: String, fn: () -> Unit)
//external fun it(name: String, fn: (dynamic) -> Any?): dynamic
//external fun xit(name: String, fn: () -> Any?)

@JsModule("kotlin-test")
@JsNonModule
private external val kotlinTestJsModule: dynamic


internal fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
   kotlinTestJsModule.kotlin.test.suite(name, ignored) {
      suiteFn()
   }
}

internal fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
   kotlinTestJsModule.kotlin.test.test(name, ignored) {
      testFn()
   }
}

internal fun suite(name: String, suiteFn: () -> Unit) = suite(name, ignored = false, suiteFn)
internal fun xsuite(name: String, suiteFn: () -> Unit) = suite(name, ignored = true, suiteFn)

internal fun test(name: String, testFn: () -> Any?) = test(name, ignored = false, testFn)
internal fun xtest(name: String, testFn: () -> Any?) = test(name, ignored = true, testFn)

package io.kotest.engine

actual fun jasmineTestFrameworkAvailable(): Boolean =
   js("typeof describe === 'function' && typeof it === 'function'") as Boolean

actual fun jasmineTestIt(
   description: String,
   timeout: Int,
   testFunction: (done: JsTestDoneCallback) -> Any?,
) {
   val t = it(description, testFunction, timeout)
   t.timeout(timeout)
}

actual fun jasmineTestXit(
   description: String,
   testFunction: (done: JsTestDoneCallback) -> Any?,
) {
   xit(description, testFunction)
}

internal actual fun jasmineTestDescribe(
   description: String,
   specDefinitions: () -> Unit,
) {
   describe(description, specDefinitions)
}

internal actual fun jasmineTestXDescribe(
   description: String,
   specDefinitions: () -> Unit,
) {
   xdescribe(description, specDefinitions)
}

// Jasmine test framework functions

private external fun it(
   description: String,
   testFunction: (done: JsTestDoneCallback) -> Any?,
   timeout: Int,
): dynamic

private external fun xit(
   description: String,
   testFunction: (done: JsTestDoneCallback) -> Any?,
)

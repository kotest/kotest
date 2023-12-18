package io.kotest.engine

actual fun jasmineTestFrameworkAvailable(): Boolean =
   js("typeof describe === 'function' && typeof it === 'function'") as Boolean

actual fun jasmineTestIt(
   description: String,
   testFunction: (implementationCallback: (errorOrNull: Throwable?) -> Unit) -> Any?,
   timeout: Int
) {
   it(description, testFunction, timeout)
}

actual fun jasmineTestXit(
   description: String,
   testFunction: (implementationCallback: (errorOrNull: Throwable?) -> Unit) -> Any?
) {
   xit(description, testFunction)
}

// Jasmine test framework functions

private external fun it(description: String, testFunction: dynamic, timeout: Int)

private external fun xit(description: String, testFunction: dynamic)

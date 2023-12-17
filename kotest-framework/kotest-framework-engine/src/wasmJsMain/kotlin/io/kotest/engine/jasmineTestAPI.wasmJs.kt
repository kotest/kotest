package io.kotest.engine

actual fun jasmineTestFrameworkAvailable(): Boolean = js("typeof describe === 'function' && typeof it === 'function'")

actual fun jasmineTestIt(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?,
   timeout: Int
) {
   @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
   it(description, testFunction as JsAny, timeout)
}

actual fun jasmineTestXit(description: String, testFunction: () -> Any?) {
   @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
   xit(description, testFunction as JsAny)
}

// Jasmine test framework functions

private external fun it(description: String, testFunction: JsAny, timeout: Int)

private external fun xit(description: String, testFunction: JsAny)

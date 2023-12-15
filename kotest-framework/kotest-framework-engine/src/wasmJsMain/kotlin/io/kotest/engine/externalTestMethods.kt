package io.kotest.engine

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual fun jsTestIt(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?,
   timeout: Int
) {
   it(description, testFunction as JsAny, timeout)
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual fun jsTestXit(description: String, testFunction: () -> Any?) {
   xit(description, testFunction as JsAny)
}

// Jasmine/Mocha test framework functions

private external fun it(description: String, testFunction: JsAny, timeout: Int)

private external fun xit(description: String, testFunction: JsAny)

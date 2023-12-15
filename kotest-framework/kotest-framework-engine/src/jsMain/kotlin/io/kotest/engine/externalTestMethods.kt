package io.kotest.engine

actual fun jsTestIt(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?,
   timeout: Int
) {
   it(description, testFunction, timeout)
}

actual fun jsTestXit(description: String, testFunction: () -> Any?) {
   xit(description, testFunction)
}

// Jasmine/Mocha test framework functions

private external fun it(description: String, testFunction: dynamic, timeout: Int)

private external fun xit(description: String, testFunction: dynamic)

package io.kotest.engine

expect fun jasmineTestFrameworkAvailable(): Boolean

// Adapters for test framework functions whose signatures differ between JS and Wasm.

expect fun jasminTestDescribe(name: String, specDefinitions: () -> Unit)

expect fun jasmineTestIt(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?
)

expect fun jasmineTestXit(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?
)

package io.kotest.engine

expect fun jasmineTestFrameworkAvailable(): Boolean

// Adapters for test framework functions whose signatures differ between JS and Wasm.

expect fun jasmineTestIt(
   description: String,
   testFunction: (implementationCallback: (errorOrNull: Throwable?) -> Unit) -> Any?,
   timeout: Int
)

expect fun jasmineTestXit(description: String, testFunction: () -> Any?)

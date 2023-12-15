package io.kotest.engine

/**
 * Defines the jasmine/mocha style test framework function names as external functions.
 * Then we can invoke them from the test engine.
 *
 * Note: At runtime, one of the supported JS test frameworks must make these functions available.
 */
external fun describe(description: String, specDefinitions: () -> Unit)
external fun xdescribe(description: String, specDefinitions: () -> Unit)

// Adapters for test framework functions whose signatures differ between JS and Wasm.

expect fun jsTestIt(
   description: String,
   testFunction: (implementationCallback: (errorOrNull: Throwable?) -> Unit) -> Any?,
   timeout: Int
)

expect fun jsTestXit(description: String, testFunction: () -> Any?)

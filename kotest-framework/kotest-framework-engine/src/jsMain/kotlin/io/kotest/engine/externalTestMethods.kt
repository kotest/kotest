package io.kotest.engine

/**
 * Defines the jasmine/mocha/karma style test function names as external functions.
 * Then we can invoke them from the test engine.
 *
 * Note: At runtime, one of the supported JS test frameworks must make these functions available.
 */
external fun describe(name: String, fn: () -> Unit)
external fun xdescribe(name: String, fn: () -> Unit)
external fun it(name: String, fn: (dynamic) -> Any?)
external fun xit(name: String, fn: () -> Any?)

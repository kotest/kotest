package io.kotest.core.js

/**
 * Defines the jasmine/mocha/karma style test function names as external functions.
 * Then we can invoke them from the test engine.
 */
external fun describe(name: String, fn: () -> Unit)
external fun xdescribe(name: String, fn: () -> Unit)
external fun it(name: String, fn: (dynamic) -> Any?)
external fun xit(name: String, fn: () -> Any?)

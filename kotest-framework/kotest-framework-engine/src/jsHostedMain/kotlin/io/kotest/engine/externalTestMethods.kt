package io.kotest.engine

/**
 * Defines the jasmine/mocha/karma style test function names as external functions.
 * Then we can invoke them from the test engine.
 *
 * Note: At runtime, one of the supported JS test frameworks must make these functions available.
 */
external fun describe(name: String, fn: () -> Unit)
external fun xdescribe(name: String, fn: () -> Unit)

expect fun jasmineIt(name: String, fn: (done: (errorOrNull: Throwable?) -> Unit) -> Any?, timeout: Int): Unit

expect fun jasmineXit(name: String, fn: () -> Any?): Unit

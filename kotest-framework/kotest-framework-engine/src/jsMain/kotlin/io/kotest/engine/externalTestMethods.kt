package io.kotest.engine

import io.kotest.common.KotestInternal

/**
 * Defines the jasmine/mocha/karma style test function names as external functions.
 * Then we can invoke them from the test engine.
 *
 * Note: At runtime, one of the supported JS test frameworks must make these functions available.
 */
@KotestInternal
external fun describe(name: String, fn: () -> Unit)

@KotestInternal
external fun xdescribe(name: String, fn: () -> Unit)

@KotestInternal
external fun it(name: String, fn: (JsTestDoneCallback) -> Unit): JsTestHandle

//@KotestInternal
//external fun it(name: String, fn: (dynamic) -> Any?): JsTestHandle

@KotestInternal
external fun xit(name: String, fn: () -> Any?)

//@KotestInternal
//external fun concurrent(name: String, fn: (JsTestDoneCallback) -> Unit)
//
//@KotestInternal
//external fun skip(name: String, fn: Promise<Any?>)

@KotestInternal
external interface JsTestHandle {
   fun timeout(timeout: Int)
}

@KotestInternal
external interface JsTestDoneCallback

internal operator fun JsTestDoneCallback.invoke(
   error: Throwable?
): dynamic = asDynamic()(error)

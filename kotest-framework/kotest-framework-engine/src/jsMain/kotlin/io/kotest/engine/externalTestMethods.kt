/**
 * Defines the jasmine/mocha/karma style test function names as external functions.
 * Then we can invoke them from the test engine.
 *
 * Note: At runtime, one of the supported JS test frameworks must make these functions available.
 */
package io.kotest.engine

import io.kotest.common.KotestInternal
import kotlin.js.Promise

@JsTestDsl
internal external val it: It

@JsTestDsl
internal external val xit: It

@JsTestDsl
internal external val describe: Describe

@JsTestDsl
internal external val xdescribe: Describe

//@KotestInternal
//external fun it(name: String, fn: (dynamic) -> Any?): JsTestHandle

//@KotestInternal
//external fun xit(name: String, fn: () -> Unit)

//@KotestInternal
//external fun concurrent(name: String, fn: (JsTestDoneCallback) -> Unit)
//
//@KotestInternal
//external fun skip(name: String, fn: Promise<Any?>)

@KotestInternal
external interface JsTestHandle {
   fun timeout(timeout: Int)
}



/** Creates a test closure */
@JsTestDsl
internal external interface It {
   /**
    * Skips running this test in the current file.
    */
   val skip: It
}

@JsTestDsl
internal interface Describe {
   /** Skips running the tests inside this `describe` for the current file */
   val skip: Describe
}

/**
 * Creates a test closure.
 *
 * @param name The name of your test
 * @param fn The function for your test
 */
@JsTestDsl
internal operator fun It.invoke(
   name: String,
   fn: (JsTestDoneCallback) -> Unit,
): JsTestHandle {
   return asDynamic()(name, fn).unsafeCast<JsTestHandle>()
}


@JsTestDsl
internal fun It.promise(
   name: String,
   fn: () -> Promise<Any?>,
): JsTestHandle {
   return asDynamic()(name, fn).unsafeCast<JsTestHandle>()
}


@JsTestDsl
internal operator fun Describe.invoke(
   name: String,
   fn: () -> Unit,
) {
   asDynamic()(name, fn)
}

// Non-functional, it's just so that IJ highlights 'it' / 'describe' usages with pretty colours
@DslMarker
internal annotation class JsTestDsl

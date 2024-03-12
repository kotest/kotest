package io.kotest.engine

import io.kotest.common.KotestInternal

expect fun jasmineTestFrameworkAvailable(): Boolean

// Adapters for test framework functions whose signatures differ between JS and Wasm.

expect fun jasmineTestIt(
   description: String,
   testFunction: (done: JsTestDoneCallback) -> Any?,
   timeout: Int
)

expect fun jasmineTestXit(
   description: String,
   testFunction: (done: JsTestDoneCallback) -> Any?
)


internal typealias JsTestDoneCallback =  (errorOrNull: Throwable?) -> Unit

//@KotestInternal
//external interface JsTestDoneCallback
//
//internal expect operator fun JsTestDoneCallback.invoke(
//   error: Throwable?
//)

//// (() => PromiseLike<any>) | (() => void) | ((done: DoneFn) => void);
//internal external interface ImplementationCallback
//internal external interface ImplementationCallbackPromise: ImplementationCallback
//internal external interface ImplementationCallbackUnit: ImplementationCallback
//internal external interface ImplementationCallbackDone: ImplementationCallback
//
//expect internal  operator fun ImplementationCallbackPromise.invoke()

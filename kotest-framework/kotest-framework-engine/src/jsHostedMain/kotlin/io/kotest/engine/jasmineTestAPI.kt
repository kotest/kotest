package io.kotest.engine

internal expect fun jasmineTestFrameworkAvailable(): Boolean

// Adapters for test framework functions whose signatures differ between JS and Wasm.

internal expect fun jasmineTestIt(
   description: String,
   // some frameworks default to a 2000 timeout,
   // here we set to a high number and use the timeout support kotest provides via coroutines
   timeout: Int = Int.MAX_VALUE,
   testFunction: (done: JsTestDoneCallback) -> Any?,
)

internal expect fun jasmineTestXit(
   description: String,
   testFunction: (done: JsTestDoneCallback) -> Any?,
)

internal expect fun jasmineTestDescribe(
   description: String,
   specDefinitions: () -> Unit,
)

internal expect fun jasmineTestXDescribe(
   description: String,
   specDefinitions: () -> Unit,
)


internal typealias JsTestDoneCallback = (errorOrNull: Throwable?) -> Unit

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

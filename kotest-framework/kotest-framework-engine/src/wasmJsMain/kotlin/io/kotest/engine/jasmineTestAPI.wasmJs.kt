package io.kotest.engine

import kotlin.js.Promise

internal actual fun jasmineTestFrameworkAvailable(): Boolean =
   js("typeof describe === 'function' && typeof it === 'function'")

internal actual fun jasmineTestIt(
   description: String,
   timeout: Int,
   testFunction: (done: JsTestDoneCallback) -> Any?,
) {
   it(description, { done ->
      callTestFunction(testFunction) {
         callDone(done, it)
      }
   }, timeout)
}

internal actual fun jasmineTestXit(
   description: String,
   testFunction: (done: JsTestDoneCallback) -> Any?
) {
   xit(description) { done ->
      callTestFunction(testFunction) {
         callDone(done, it)
      }
   }
}

internal actual fun jasmineTestDescribe(
   description: String,
   specDefinitions: () -> Unit,
) {
   describe(description, specDefinitions)
}

internal actual fun jasmineTestXDescribe(
   description: String,
   specDefinitions: () -> Unit,
) {
   xdescribe(description, specDefinitions)
}

private fun callTestFunction(
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?,
   done: (errorOrNull: Throwable?) -> Unit
): JsAny? =
   try {
      (testFunction(done) as? Promise<*>)?.catch { exception ->
         val jsException = exception
            .toThrowableOrNull()
            ?.toJsError()
            ?: exception
         Promise.reject(jsException)
      }
   } catch (exception: Throwable) {
      jsThrow(exception.toJsError())
   }

private fun callDone(
   done: (errorOrNull: JsAny?) -> Unit,
   errorOrNull: Throwable?
) {
   done(errorOrNull?.toJsError())
}

private fun jsThrow(jsException: JsAny): Nothing =
   js("{ throw jsException; }")

private fun throwableToJsError(message: String, stack: String): JsAny =
   js("{ const e = new Error(); e.message = message; e.stack = stack; return e; }")

private fun Throwable.toJsError(): JsAny =
   throwableToJsError(message ?: "", stackTraceToString())

// Jasmine test framework functions

private external fun it(
   description: String,
   testFunction: (done: (errorOrNull: JsAny?) -> Unit) -> JsAny?,
   timeout: Int
)

private external fun xit(
   description: String,
   testFunction: (done: (errorOrNull: JsAny?) -> Unit) -> JsAny?
)

private external fun describe(
   description: String,
   specDefinitions: () -> Unit,
)

private external fun xdescribe(
   description: String,
   specDefinitions: () -> Unit,
)

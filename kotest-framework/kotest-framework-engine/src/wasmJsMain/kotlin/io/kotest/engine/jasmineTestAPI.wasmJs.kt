package io.kotest.engine

import kotlin.js.Promise

actual fun jasmineTestFrameworkAvailable(): Boolean = js("typeof describe === 'function' && typeof it === 'function'")

actual fun jasmineTestIt(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?,
   timeout: Int
) {
   it(description, { done ->
      callTest(testFunction) {
         callImplementationCallback(done, it)
      }
   }, timeout)
}

actual fun jasmineTestXit(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?
) {
   xit(description) { done ->
      callTest(testFunction) {
         callImplementationCallback(done, it)
      }
   }
}

private fun callTest(
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

private fun callImplementationCallback(
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

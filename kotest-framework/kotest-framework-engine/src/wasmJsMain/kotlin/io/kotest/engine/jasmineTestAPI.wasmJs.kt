package io.kotest.engine

import kotlin.js.Promise

actual fun jasmineTestFrameworkAvailable(): Boolean = js("typeof describe === 'function' && typeof it === 'function'")

actual fun jasminTestDescribe(name: String, specDefinitions: () -> Unit) {
   describe(name, specDefinitions)
}

actual fun jasmineTestIt(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?
) {
   it(description) { done ->
      callTestFunction(testFunction) {
         callDone(done, it)
      }
   }
}

actual fun jasmineTestXit(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?
) {
   xit(description) { done ->
      callTestFunction(testFunction) {
         callDone(done, it)
      }
   }
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

@Suppress("UNUSED_PARAMETER")
private fun jsThrow(jsException: JsAny): Nothing =
   js("{ throw jsException; }")

@Suppress("UNUSED_PARAMETER")
private fun throwableToJsError(message: String, stack: String): JsAny =
   js("{ const e = new Error(); e.message = message; e.stack = stack; return e; }")

private fun Throwable.toJsError(): JsAny =
   throwableToJsError(message ?: "", stackTraceToString())

// Jasmine test framework functions

@Suppress("UNUSED_PARAMETER")
private fun describe(description: String, specDefinitions: () -> Unit) {
   // Here we disable the default 2s timeout and use the timeout support Kotest provides via coroutines.
   js("describe(description, function () { this.timeout(0); specDefinitions(); })")
}

private external fun it(description: String, testFunction: (done: (errorOrNull: JsAny?) -> Unit) -> JsAny?)

private external fun xit(description: String, testFunction: (done: (errorOrNull: JsAny?) -> Unit) -> JsAny?)

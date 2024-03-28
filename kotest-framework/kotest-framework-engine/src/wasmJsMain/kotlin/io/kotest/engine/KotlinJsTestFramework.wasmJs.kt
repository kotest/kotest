package io.kotest.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

internal fun kotlinJsTestFrameworkAvailable(): Boolean =
   js("typeof describe === 'function' && typeof it === 'function'")

internal actual val kotlinJsTestFramework: KotlinJsTestFramework = object : KotlinJsTestFramework {
   override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
      if (ignored) {
         xdescribe(name, suiteFn)
      } else {
         describe(name, suiteFn)
      }
   }

   override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
      if (ignored) {
         xit(name) {
            callTest(testFn)
         }
      } else {
         it(name) {
            callTest(testFn)
         }
      }
   }

   private fun callTest(testFn: () -> Any?): Promise<*>? =
      try {
         (testFn() as? Promise<*>)?.catch { exception ->
            val jsException = exception
               .toThrowableOrNull()
               ?.toJsError()
               ?: exception
            Promise.reject(jsException)
         }
      } catch (exception: Throwable) {
         jsThrow(exception.toJsError())
      }
}

internal actual fun CoroutineScope.testFunctionPromise(testFunction: suspend () -> Unit): Any? =
   promise { testFunction() }

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
private fun describe(description: String, suiteFn: () -> Unit) {
   // Here we disable the default 2s timeout and use the timeout support which Kotest provides via coroutines.
   // The strange invocation is necessary to avoid using a JS arrow function which would bind `this` to a
   // wrong scope: https://stackoverflow.com/a/23492442/2529022
   js("describe(description, function () { this.timeout(0); suiteFn(); })")
}

private external fun xdescribe(name: String, testFn: () -> Unit)
private external fun it(name: String, testFn: () -> JsAny?)
private external fun xit(name: String, testFn: () -> JsAny?)

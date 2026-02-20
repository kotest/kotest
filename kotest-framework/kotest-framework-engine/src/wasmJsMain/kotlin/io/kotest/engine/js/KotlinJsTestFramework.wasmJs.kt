package io.kotest.engine.js

import kotlin.js.Promise

/**
 * An implementation of [KotlinJsTestFramework] for Kotlin/WasmJS that outputs directly to
 * Jasmine-compatible test/suite methods.
 *
 * This implementation handles nuances in the way WasmJS handles exceptions thrown in tests.
 */
@OptIn(ExperimentalWasmJsInterop::class)
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
         println("Caught exception in test: ${exception.message}")
         jsThrow(exception.toJsError())
      }
}

// jasmine, and other test frameworks that have a similar API, define these functions
// as global functions, so we declare them as external functions here.

@OptIn(ExperimentalWasmJsInterop::class)
@Suppress("UNUSED_PARAMETER")
private fun describe(description: String, suiteFn: () -> Unit) {
   // Here we disable the default 2s timeout and use the timeout support which Kotest provides via coroutines.
   // The strange invocation is necessary to avoid using a JS arrow function which would bind `this` to a
   // wrong scope: https://stackoverflow.com/a/23492442/2529022
   js("describe(description, function () { this.timeout(0); suiteFn(); })")
}

private external fun xdescribe(description: String, suiteFn: () -> Unit)

@OptIn(ExperimentalWasmJsInterop::class)
private external fun it(name: String, testFn: () -> JsAny?)

@OptIn(ExperimentalWasmJsInterop::class)
private external fun xit(name: String, testFn: () -> JsAny?)


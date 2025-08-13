package io.kotest.engine.js

/**
 * An implementation of [FrameworkAdapter] for Jasmine-like test frameworks that
 * support `describe`, `xdescribe`, `it`, and `xit` global functions.
 */
internal class JasmineLikeAdapter : FrameworkAdapter {
   override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
      if (ignored) {
         xdescribe(name = name, suiteFn = suiteFn)
      } else {
         describe(name = name, suiteFn = suiteFn)
      }
   }

   override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
      if (ignored) {
         xit(name = name, testFn = testFn)
      } else {
         it(name = name, testFn = testFn)
      }
   }
}

// jasmine, and other test frameworks that have a similar API, define these functions
// as global functions, so we declare them as external functions here.

@Suppress("UNUSED_PARAMETER")
private fun describe(name: String, suiteFn: () -> Unit) {
   // Here we disable the default 2s timeout and use the timeout support which Kotest provides via coroutines.
   // The strange invocation is necessary to avoid using a JS arrow function which would bind `this` to a
   // wrong scope: https://stackoverflow.com/a/23492442/2529022
   js("describe(name, function () { this.timeout(0); suiteFn(); })")
}

private external fun xdescribe(name: String, suiteFn: () -> Unit)
private external fun it(name: String, testFn: () -> Any?)
private external fun xit(name: String, testFn: () -> Any?)

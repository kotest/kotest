package io.kotest.engine

actual fun jasmineTestFrameworkAvailable(): Boolean =
   js("typeof describe === 'function' && typeof it === 'function'") as Boolean

actual fun jasminTestDescribe(name: String, specDefinitions: () -> Unit) {
   describe(name, specDefinitions)
}

actual fun jasmineTestIt(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?
) {
   it(description, testFunction)
}

actual fun jasmineTestXit(
   description: String,
   testFunction: (done: (errorOrNull: Throwable?) -> Unit) -> Any?
) {
   xit(description, testFunction)
}

// Jasmine test framework functions

@Suppress("UNUSED_PARAMETER")
private fun describe(description: String, specDefinitions: () -> Unit) {
   // Here we disable the default 2s timeout and use the timeout support Kotest provides via coroutines.
   js("describe(description, function () { this.timeout(0); specDefinitions(); })")
}


private external fun it(description: String, testFunction: dynamic)

private external fun xit(description: String, testFunction: dynamic)

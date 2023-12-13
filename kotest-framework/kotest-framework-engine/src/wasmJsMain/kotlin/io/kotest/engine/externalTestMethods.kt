package io.kotest.engine

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual fun jasmineIt(name: String, fn: (done: (errorOrNull: Throwable?) -> Unit) -> Any?, timeout: Int): Unit =
   it(name, fn as JsAny, timeout)

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual fun jasmineXit(name: String, fn: () -> Any?): Unit = xit(name, fn as JsAny)

private external fun it(name: String, fn: JsAny, timeout: Int)

private external fun xit(name: String, fn: JsAny)

package io.kotest.engine

actual fun jasmineIt(name: String, fn: (done: (errorOrNull: Throwable?) -> Unit) -> Any?, timeout: Int): Unit =
   it(name, fn, timeout)

actual fun jasmineXit(name: String, fn: () -> Any?): Unit = xit(name, fn)

private external fun it(name: String, fn: dynamic, timeout: Int): Unit

private external fun xit(name: String, fn: dynamic): Unit

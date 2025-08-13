package io.kotest.engine.js

@Suppress("UNUSED_PARAMETER")
internal fun jsThrow(jsException: JsAny): Nothing =
   js("{ console.log('foo'); throw jsException; }")

@Suppress("UNUSED_PARAMETER")
internal fun throwableToJsError(message: String, stack: String): JsAny =
   js("{ const e = new Error(); e.message = message; e.stack = stack; return e; }")

internal fun Throwable.toJsError(): JsAny =
   throwableToJsError(message ?: "", stackTraceToString())

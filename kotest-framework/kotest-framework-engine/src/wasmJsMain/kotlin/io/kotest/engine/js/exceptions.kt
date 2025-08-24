package io.kotest.engine.js

@Suppress("UNUSED_PARAMETER")
internal fun jsThrow(jsException: JsAny): Nothing =
   js("{ console.log('foo'); throw jsException; }")

/**
 * Creates a JavaScript Error instance with the given message and stack info.
 * See https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Error/Error
 */
@Suppress("UNUSED_PARAMETER")
internal fun throwableToJsError(message: String, stack: String): JsAny =
   js("{ const e = new Error(); e.message = message; e.stack = stack; return e; }")

/**
 * Creates a JavaScript Error instance with the message and stack trace of this Throwable.
 * See https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Error/Error
 */
internal fun Throwable.toJsError(): JsAny =
   throwableToJsError(message ?: "", stackTraceToString())

@Suppress("UNUSED_PARAMETER")
internal fun printStderr(message: String) {
   js("process.stderr.write(message)")
}

@Suppress("UNUSED_PARAMETER")
internal fun exitProcess(status: Int) {
   js("process.exit(status)")
}

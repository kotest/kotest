package io.kotest.engine.js

import kotlinx.coroutines.CoroutineScope

/**
 * Returns an invocation of [block] as a Promise.
 *
 * Since there is no common Promise type for WasmJS and JS implementations, we must return an Any.
 */
internal expect fun promise(block: suspend CoroutineScope.() -> Unit): Any?

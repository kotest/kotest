package io.kotest.engine.test.interceptors

/**
 * Returns true if the current runtime is D8 (V8's standalone JavaScript shell).
 *
 * D8's [setTimeout] implementation fires callbacks in registration order, ignoring the delay value.
 * This breaks Kotest's [TimeoutInterceptor] which registers a [withTimeout] callback before the
 * test body runs, causing tests that use [delay] to time out immediately.
 */
internal expect fun isD8Runtime(): Boolean

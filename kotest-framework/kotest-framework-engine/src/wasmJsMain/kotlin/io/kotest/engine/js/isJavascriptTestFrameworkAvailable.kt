package io.kotest.engine.js

/**
 * Returns true if a Jasmine-like Javascript test framework is available.
 *
 * This depends if we have a JS runtime, and if we are running inside the browser or node/js.
 */
internal fun isJavascriptTestFrameworkAvailable(): Boolean =
   js("typeof describe === 'function' && typeof it === 'function'")

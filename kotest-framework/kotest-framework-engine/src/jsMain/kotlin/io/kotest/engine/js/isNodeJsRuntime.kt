package io.kotest.engine.js

/**
 * Returns true if the current runtime environment is Node.js.
 *
 * @see https://stackoverflow.com/a/31090240
 */
internal fun isNodeJsRuntime(): Boolean =
   js("(new Function('try { return this === global; } catch(e) { return false; }'))()")

/**
 * Returns true if a Jasmine-like JavaScript test framework is available.
 *
 * This depends on if we have a JS runtime and if we are running inside the browser or node/js.
 */
internal fun isJavaScriptTestFrameworkAvailable(): Boolean =
   js("typeof describe === 'function' && typeof it === 'function'")

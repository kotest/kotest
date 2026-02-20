package io.kotest.engine.js

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

/**
 * Returns true if a Jasmine-like JavaScript test framework is available.
 *
 * This depends on if we have a JS runtime and if we are running inside a browser or node.js.
 */
@OptIn(ExperimentalWasmJsInterop::class)
internal fun isJavaScriptTestFrameworkAvailable(): Boolean =
   js("typeof describe === 'function' && typeof it === 'function'")

/**
 * Returns true if the current runtime environment is Node.js.
 *
 * @see https://stackoverflow.com/a/31090240
 */
@OptIn(ExperimentalWasmJsInterop::class)
internal fun isNodeJsRuntime(): Boolean =
   js("(new Function('try { return this === global; } catch(e) { return false; }'))()")

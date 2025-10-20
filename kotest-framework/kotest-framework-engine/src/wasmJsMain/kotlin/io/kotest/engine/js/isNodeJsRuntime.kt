package io.kotest.engine.js

/**
 * Returns true if the current runtime environment is NodeJS.
 *
 * @see https://stackoverflow.com/a/31090240
 */
internal fun isNodeJsRuntime(): Boolean =
   js("(new Function('try { return this === global; } catch(e) { return false; }'))()")

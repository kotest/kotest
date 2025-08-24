package io.kotest.engine.spec.execution

import io.kotest.core.spec.Spec
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.js.KotlinJsSpecExecutor

/**
 * Javascript can be compiled to both wasm and js. In the case of wasm, although the language is JS,
 * the runtime is not necessarily in a browser. If we are not operating in a hosted JS environment (eg D8)
 * the we use  the [SingleInstanceSpecExecutor] which is the default executor for Kotest.
 */
internal actual fun specExecutor(context: EngineContext, spec: Spec): SpecExecutor {
   return if (kotlinJsTestFrameworkAvailable()) KotlinJsSpecExecutor(context) else SingleInstanceSpecExecutor(context)
}

/**
 * Detects if a jasmine-like Kotlin JS test framework is available by checking
 * for the presence of `describe` and `it` functions.
 */
private fun kotlinJsTestFrameworkAvailable(): Boolean =
   js("typeof describe === 'function' && typeof it === 'function'")

/**
 * Returns true if the current runtime environment is NodeJS.
 *
 * @see https://stackoverflow.com/a/31090240
 */
internal fun isNodeJs(): Boolean =
   js("(new Function('try { return this === global; } catch(e) { return false; }'))()")

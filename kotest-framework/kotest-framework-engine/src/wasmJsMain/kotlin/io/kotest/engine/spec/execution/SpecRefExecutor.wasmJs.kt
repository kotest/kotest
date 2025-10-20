package io.kotest.engine.spec.execution

import io.kotest.core.spec.Spec
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.js.KotlinJsSpecExecutor
import io.kotest.engine.js.isJavascriptTestFrameworkAvailable

/**
 * Javascript can be compiled to both wasm and js. In the case of wasm, although the language is JS,
 * the runtime is not necessarily in a browser. If we are not operating in a hosted JS environment (eg D8)
 * the we use  the [SingleInstanceSpecExecutor] which is the default executor for Kotest.
 */
internal actual fun specExecutor(context: EngineContext, spec: Spec): SpecExecutor {
   return if (isJavascriptTestFrameworkAvailable()) KotlinJsSpecExecutor(context) else SingleInstanceSpecExecutor(context)
}

package io.kotest.engine.js

// Part of the Kotlin/JS test infra.
// used to wrap framework adapters to fix issues or add functionality.
internal external interface KotlinTestNamespace {
   val adapterTransformer: ((FrameworkAdapter) -> FrameworkAdapter)?
}

// Part of the Kotlin/JS test infra.
@JsName("kotlinTest")
internal external val kotlinTestNamespace: KotlinTestNamespace

package io.kotest.engine.js

import io.kotest.engine.spec.AbstractSpec

/**
 * This is invoked in the generated javascript test.
 */
expect fun executeSpec(spec: AbstractSpec)

/**
 * Executed when the engine is first setup (by virtue of being assigned to a top level val), this
 * configure method is used to setup the javascript runtime. On the JVM this is a no-op.
 */
expect fun configureRuntime()

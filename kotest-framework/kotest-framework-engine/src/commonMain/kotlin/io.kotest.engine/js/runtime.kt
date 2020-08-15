package io.kotest.engine.js

import io.kotest.core.spec.Spec

/**
 * This is invoked in the generated javascript test.
 */
expect fun executeSpec(spec: Spec)

/**
 * Executed when the engine is first setup (by virtue of being assigned to a top level val), this
 * configure method is used to setup the javascript runtime. On the JVM this is a no-op.
 */
expect fun configureRuntime()

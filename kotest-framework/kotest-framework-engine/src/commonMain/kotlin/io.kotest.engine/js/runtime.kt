package io.kotest.engine.js

import io.kotest.core.spec.Spec

/**
 * This is invoked in the generated javascript test.
 */
expect fun executeSpec(spec: Spec)

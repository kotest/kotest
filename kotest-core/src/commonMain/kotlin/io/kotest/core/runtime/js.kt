package io.kotest.core.runtime

import io.kotest.core.spec.Spec

/**
 * This is invoked in generated javascript code.
 */
expect fun executeSpec(spec: Spec)

expect fun configureRuntime()

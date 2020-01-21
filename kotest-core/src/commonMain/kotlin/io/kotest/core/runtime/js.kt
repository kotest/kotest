package io.kotest.core.runtime

import io.kotest.core.spec.Spec

/**
 * This is invoked at compile time by the Javascript compiler to generate calls to kotest for each test case.
 * On other platforms this method is a no-op.
 */
expect fun executeSpec(spec: Spec)

expect fun configureRuntime()

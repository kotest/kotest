package io.kotest.core.runtime

import io.kotest.core.spec.Spec

/**
 * This is invoked at compile time by the Javascript compiler to generate calls to kotest for each test case.
 *
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
actual fun executeSpec(spec: Spec) {
   JsTestEngine().execute(spec)
}

actual fun configureRuntime() {
   setAdapter(KotestAdapter)
}

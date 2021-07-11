@file:Suppress("unused")

package io.kotest.engine.execution

import io.kotest.core.spec.Spec
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * Entry point for native tests.
 * This method is invoked by the compiler plugin.
 */
@DelicateCoroutinesApi
fun executeSpec(spec: Spec) {
   println("Executing test $spec")
}

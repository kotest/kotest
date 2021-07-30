@file:Suppress("unused")

package io.kotest.engine.execution

import io.kotest.core.spec.Spec
import io.kotest.engine.NativeEngine
import io.kotest.engine.NativeEngineConfig
import io.kotest.engine.TestSuite
import io.kotest.engine.preconditions.IsNotNestedSpecStyle
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * Entry point for native tests.
 * This method is invoked by the compiler plugin.
 */
@DelicateCoroutinesApi
fun executeSpec(spec: Spec) {
   val engine = NativeEngine(NativeEngineConfig(listOf(IsNotNestedSpecStyle)))
   engine.execute(TestSuite(listOf(spec)))
}

@file:Suppress("unused")

package io.kotest.engine

import io.kotest.core.spec.Spec
import io.kotest.engine.preconditions.IsNotNestedSpecStyle
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * Entry point for native tests.
 *
 * This method is invoked by the compiler plugin and the contract must remain.
 */
@DelicateCoroutinesApi
fun nativeEntryPoint(spec: Spec) {
   val engine = NativeEngine(NativeEngineConfig(listOf(IsNotNestedSpecStyle)))
   engine.execute(TestSuite(listOf(spec)))
}

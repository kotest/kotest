@file:Suppress("unused")

package io.kotest.engine

import io.kotest.core.spec.Spec
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * Entry point for native tests.
 *
 * This method is invoked by the compiler plugin and the contract must remain.
 */
@DelicateCoroutinesApi
fun nativeEntryPoint(spec: Spec) {
   val engine = TestEngine(TestEngineConfig.default())
   engine.execute(TestSuite(listOf(spec), emptyList()))
}

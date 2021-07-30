package io.kotest.engine

import io.kotest.core.spec.Spec
import io.kotest.engine.preconditions.IsNotNestedSpecStyle
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * Entry point for JS tests.
 * This method is invoked by the compiler plugin.
 */
@DelicateCoroutinesApi
fun executeSpec(spec: Spec) {
   val config = TestEngineConfig(
      emptyList(),
      emptyList(),
      NoopTestEngineListener,
      null,
      false,
      listOf(IsNotNestedSpecStyle)
   )
   val engine = TestEngine(config)
   engine.execute(TestSuite(listOf(spec)))
}

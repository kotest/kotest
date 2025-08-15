package io.kotest.engine.spec.execution

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.engine.interceptors.EngineContext

@Suppress("DEPRECATION")
internal actual fun specExecutor(context: EngineContext, spec: Spec): SpecExecutor {
   return when (context.specConfigResolver.isolationMode(spec)) {
      IsolationMode.SingleInstance -> SingleInstanceSpecExecutor(context)
      IsolationMode.InstancePerRoot -> InstancePerRootSpecExecutor(context)
      IsolationMode.InstancePerLeaf -> InstancePerLeafSpecExecutor(context)
      IsolationMode.InstancePerTest -> InstancePerTestSpecExecutor(context)
   }
}

package io.kotest.engine

import io.kotest.common.JVMOnly
import io.kotest.core.config.AbstractProjectConfig

// no-op when not on the JVM
@JVMOnly
internal actual fun writeFailuresIfEnabled(context: TestEngineContext) {
}

// no-op when not on the JVM
@JVMOnly
internal actual fun loadSystemProperties() {
}

// no-op when not on the JVM, so just returns the input.
@JVMOnly
internal actual fun resolveProjectConfig(
   projectConfig: AbstractProjectConfig?,
   specFqns: Set<String>,
): AbstractProjectConfig? = projectConfig

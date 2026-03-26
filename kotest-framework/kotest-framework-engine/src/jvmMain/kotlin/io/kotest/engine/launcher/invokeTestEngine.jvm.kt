@file:Suppress("unused")

package io.kotest.engine.launcher

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef

actual suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?) {
   error("JVM execution is via JUnit Platform")
}

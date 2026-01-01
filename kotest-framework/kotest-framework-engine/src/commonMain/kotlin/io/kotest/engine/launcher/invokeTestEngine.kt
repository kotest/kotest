package io.kotest.engine.launcher

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.engine.TestEngineLauncher

/**
 * Accepts a [TestEngineLauncher] created by the compiler KSP plugin, and then launches it
 * using platform specific logic. Eg, on JS it will launch using a JS promise.
 */
expect suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?)

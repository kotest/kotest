package io.kotest.engine.launcher

import io.kotest.common.KotestInternal
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef

/**
 * Launches the given [specs] with any supplied [config].
 */
@KotestInternal
expect suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?)

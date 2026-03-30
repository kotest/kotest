package io.kotest.engine.extensions.invocationcount

import io.kotest.common.syspropOrEnv
import io.kotest.core.extensions.InvocationCountExtension
import io.kotest.engine.config.KotestEngineEnvVars

internal object SystemPropertyOrEnvInvocationCountExtension : InvocationCountExtension {
   override fun getInvocationCount(): Int? = syspropOrEnv(KotestEngineEnvVars.INVOCATION_COUNT)?.toIntOrNull()
}

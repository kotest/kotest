package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef

interface SpecRefInterceptor {
   suspend fun intercept(fn: suspend (SpecRef) -> Unit): suspend (SpecRef) -> Unit
}

interface SpecExecutionInterceptor {
   suspend fun intercept(fn: suspend (Spec) -> Unit): suspend (Spec) -> Unit
}

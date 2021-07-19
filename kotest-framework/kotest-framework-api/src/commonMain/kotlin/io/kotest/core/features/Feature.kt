package io.kotest.core.features

import io.kotest.core.execution.ExecutionContext

interface Feature<T> {
   fun install(context: ExecutionContext): T
}

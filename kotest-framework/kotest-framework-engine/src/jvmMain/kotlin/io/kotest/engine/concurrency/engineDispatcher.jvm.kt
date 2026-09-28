package io.kotest.engine.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual fun concurrentExecutionDispatcher(): CoroutineDispatcher = Dispatchers.Default

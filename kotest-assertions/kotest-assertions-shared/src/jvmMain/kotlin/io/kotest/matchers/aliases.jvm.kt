package io.kotest.matchers

import kotlin.coroutines.CoroutineContext

@Deprecated("Use io.kotest.assertions.errorCollectorContextElement. Will be removed in a future release.")
val errorCollectorContextElement: CoroutineContext.Element get() = io.kotest.assertions.errorCollectorContextElement

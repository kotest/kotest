package io.kotest.assertions

import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark

typealias SuspendingPredicate<T> = suspend (T) -> Boolean

typealias SuspendingProducer<T> = suspend () -> T




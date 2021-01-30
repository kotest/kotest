package io.kotest.assertions

typealias SuspendingPredicate<T> = suspend (T) -> Boolean

typealias SuspendingProducer<T> = suspend () -> T




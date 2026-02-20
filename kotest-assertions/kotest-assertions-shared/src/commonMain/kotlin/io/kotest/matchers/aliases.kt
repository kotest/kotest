package io.kotest.matchers

import io.kotest.assertions.BasicErrorCollector

@Deprecated("Use io.kotest.assertions.AssertionCounter. Will be removed in a future release.")
typealias AssertionCounter = io.kotest.assertions.AssertionCounter

@Deprecated("Use io.kotest.assertions.assertionCounter. Will be removed in a future release.")
val assertionCounter: AssertionCounter get() = io.kotest.assertions.assertionCounter

@Deprecated("Use io.kotest.assertions.ErrorCollectionMode. Will be removed in a future release.")
typealias ErrorCollectionMode = io.kotest.assertions.ErrorCollectionMode

@Deprecated("Use io.kotest.assertions.ErrorCollector. Will be removed in a future release.")
typealias ErrorCollector = io.kotest.assertions.ErrorCollector

@Deprecated("Use io.kotest.assertions.errorCollector. Will be removed in a future release.")
val errorCollector: ErrorCollector get() = io.kotest.assertions.errorCollector

@Deprecated("Use io.kotest.assertions.BasicErrorCollector. Will be removed in a future release.")
typealias BasicErrorCollector = BasicErrorCollector

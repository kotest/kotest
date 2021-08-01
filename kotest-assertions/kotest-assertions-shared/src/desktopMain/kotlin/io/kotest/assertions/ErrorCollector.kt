package io.kotest.assertions

import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
actual val errorCollector: ErrorCollector = NoopErrorCollector

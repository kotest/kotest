package io.kotest.assertions

import kotlin.native.concurrent.ThreadLocal

actual val errorCollector: ErrorCollector = NativeErrorCollector

@ThreadLocal
object NativeErrorCollector : BasicErrorCollector()

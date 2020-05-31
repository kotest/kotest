package io.kotest.assertions

@ThreadLocal
actual val errorCollector: ErrorCollector = BasicErrorCollector

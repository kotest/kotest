package io.kotest.assertions

actual val errorCollector: ErrorCollector = JsErrorCollector

object JsErrorCollector : BasicErrorCollector()

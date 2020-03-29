package io.kotest.assertions.json

sealed class JsonCountableElement {

    class JsonKeyValuePairs internal constructor(val count: Int) : JsonCountableElement()
}

val Int.jsonKeyValueEntries get() = JsonCountableElement.JsonKeyValuePairs(this)

package io.kotest.data

internal actual val Function<*>.paramNames: List<String>
   get() = emptyList() // kotlin-reflect doesn't support the `reflect()` function on JS

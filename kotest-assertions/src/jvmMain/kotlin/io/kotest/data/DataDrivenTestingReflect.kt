package io.kotest.data

import kotlin.reflect.jvm.reflect

internal actual val Function<*>.paramNames: List<String>
   get() = reflect()?.parameters?.mapNotNull { it.name } ?: emptyList()

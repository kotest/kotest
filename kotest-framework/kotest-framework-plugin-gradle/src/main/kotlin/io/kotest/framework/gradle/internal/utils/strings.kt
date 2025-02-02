package io.kotest.framework.gradle.internal.utils

import java.util.*

internal fun String.uppercaseFirstChar(): String =
   replaceFirstChar { it.uppercase(Locale.ROOT) }

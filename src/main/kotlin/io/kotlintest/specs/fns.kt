package io.kotlintest.specs

internal fun sanitizeSpecName(name: String) = name.replace("(", " ").replace(")", " ")
package io.kotlintest.specs

fun sanitizeSpecName(name: String) = name.replace("(", " ").replace(")", " ")
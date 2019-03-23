package io.kotlintest

import kotlin.reflect.KClass

actual fun KClass<*>.qualifiedSpecName(): String = this.simpleName ?: "UnknownClass"
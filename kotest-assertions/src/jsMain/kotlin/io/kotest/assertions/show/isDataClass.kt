package io.kotest.assertions.show

import kotlin.reflect.KClass

// not supported in JS yet
actual fun <T : Any> KClass<T>.isDataClass(): Boolean = false


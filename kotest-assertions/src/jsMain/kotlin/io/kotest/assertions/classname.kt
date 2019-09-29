package io.kotest.assertions

import kotlin.reflect.KClass

actual fun <T : Any> KClass<T>.classname(): String = simpleName ?: "<anon>"

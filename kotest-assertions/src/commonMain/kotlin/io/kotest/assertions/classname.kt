package io.kotest.assertions

import kotlin.reflect.KClass

expect fun <T : Any> KClass<T>.classname(): String

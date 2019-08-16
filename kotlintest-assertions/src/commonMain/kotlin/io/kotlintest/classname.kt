package io.kotlintest

import kotlin.reflect.KClass

expect fun <T : Any> KClass<T>.classname(): String

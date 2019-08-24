package io.kotlintest.assertions

import kotlin.reflect.KClass

expect fun <T : Any> KClass<T>.classname(): String

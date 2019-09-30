package io.kotest.assertions

import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

actual fun <T : Any> KClass<T>.classname(): String = this.qualifiedName ?: this.jvmName

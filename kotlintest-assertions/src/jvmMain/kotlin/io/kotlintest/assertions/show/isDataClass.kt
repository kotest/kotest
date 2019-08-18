package io.kotlintest.assertions.show

import kotlin.reflect.KClass

actual fun <T : Any> KClass<T>.isDataClass(): Boolean = this.isData

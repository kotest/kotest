package io.kotlintest.matchers.reflection

import kotlin.reflect.KProperty

inline fun <reified T> KProperty<*>.shouldBeOfType() = this.returnType.shouldBeOfType<T>()
inline fun <reified T> KProperty<*>.shouldNotBeOfType() = this.returnType.shouldNotBeOfType<T>()
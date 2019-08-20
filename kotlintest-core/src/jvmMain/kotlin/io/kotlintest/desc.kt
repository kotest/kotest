package io.kotlintest

import kotlin.reflect.KClass

fun Description.Companion.fromKlass(klass: KClass<out Spec>) = spec(klass.qualifiedName!!)

package io.kotest.core

import io.kotest.SpecClass
import kotlin.reflect.KClass

actual fun Description.Companion.fromSpecClass(klass: KClass<out SpecClass>): Description = spec(klass.java.name)

package io.kotest.core

import io.kotest.Description
import kotlin.reflect.KClass

actual fun Description.Companion.fromSpecClass(klass: KClass<*>): Description = spec(klass.java.name)

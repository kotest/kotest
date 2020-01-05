package io.kotest.core

import kotlin.reflect.KClass

// in JS we don't have the qualified name
actual fun Description.Companion.fromSpecClass(klass: KClass<*>): Description = Description.spec(klass.simpleName ?: "<anon>")

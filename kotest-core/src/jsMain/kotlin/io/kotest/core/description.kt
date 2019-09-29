package io.kotest.core

import io.kotest.Description
import io.kotest.Spec
import kotlin.reflect.KClass

// in JS we don't have the qualified name
actual fun Description.Companion.fromSpecClass(klass: KClass<out Spec>): Description = Description.spec(klass.simpleName ?: "<anon>")

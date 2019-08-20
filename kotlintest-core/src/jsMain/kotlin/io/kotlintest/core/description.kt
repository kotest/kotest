package io.kotlintest.core

import io.kotlintest.Description
import io.kotlintest.Spec
import kotlin.reflect.KClass

// in JS we don't have the qualified name
actual fun Description.Companion.fromSpecClass(klass: KClass<out Spec>): Description = Description.spec(klass.simpleName ?: "<anon>")

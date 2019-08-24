package io.kotlintest.core

import io.kotlintest.Description
import io.kotlintest.Spec
import kotlin.reflect.KClass

expect fun Description.Companion.fromSpecClass(klass: KClass<out Spec>): Description

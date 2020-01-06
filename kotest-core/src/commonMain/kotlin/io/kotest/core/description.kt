package io.kotest.core

import io.kotest.SpecClass
import kotlin.reflect.KClass

expect fun Description.Companion.fromSpecClass(klass: KClass<out SpecClass>): Description

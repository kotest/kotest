package io.kotest.core

import io.kotest.Description
import kotlin.reflect.KClass

expect fun Description.Companion.fromSpecClass(klass: KClass<*>): Description

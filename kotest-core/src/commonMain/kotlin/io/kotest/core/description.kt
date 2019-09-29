package io.kotest.core

import io.kotest.Description
import io.kotest.Spec
import kotlin.reflect.KClass

expect fun Description.Companion.fromSpecClass(klass: KClass<out Spec>): Description

package io.kotest.assertions.show

import kotlin.reflect.KClass

expect fun KClassShow(): Show<KClass<*>>

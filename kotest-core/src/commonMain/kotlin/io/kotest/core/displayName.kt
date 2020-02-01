package io.kotest.core

import kotlin.reflect.KClass

expect fun KClass<*>.displayName(): String?

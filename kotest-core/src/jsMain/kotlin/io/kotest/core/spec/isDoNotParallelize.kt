package io.kotest.core.spec

import kotlin.reflect.KClass

actual fun KClass<*>.isDoNotParallelize(): Boolean = false

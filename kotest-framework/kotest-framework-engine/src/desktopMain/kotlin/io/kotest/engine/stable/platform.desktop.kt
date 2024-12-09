package io.kotest.engine.stable

import kotlin.reflect.KClass

actual fun isPlatformStable(kclass: KClass<*>): Boolean = false

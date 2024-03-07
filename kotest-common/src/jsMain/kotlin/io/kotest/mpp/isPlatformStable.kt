package io.kotest.mpp

import kotlin.reflect.KClass

actual fun isPlatformStable(kclass: KClass<*>): Boolean = false

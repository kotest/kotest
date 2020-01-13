package io.kotest.core.spec

import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

actual fun KClass<*>.isDoNotParallelize(): Boolean = findAnnotation<DoNotParallelize>() != null

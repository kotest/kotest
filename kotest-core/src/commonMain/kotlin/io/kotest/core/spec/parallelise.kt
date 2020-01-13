package io.kotest.core.spec

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DoNotParallelize

expect fun KClass<*>.isDoNotParallelize(): Boolean

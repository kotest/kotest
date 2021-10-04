package io.kotest.core.extensions

import kotlin.reflect.KClass

@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ApplyExtension(vararg val factory: KClass<out ExtensionFactory>)

package io.kotest.core.spec

import io.kotest.mpp.annotation
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DoNotParallelize

fun KClass<*>.isDoNotParallelize(): Boolean = annotation<DoNotParallelize>() != null

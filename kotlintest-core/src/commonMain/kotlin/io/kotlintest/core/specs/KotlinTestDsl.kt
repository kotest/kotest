package io.kotlintest.core.specs

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@DslMarker
annotation class KotlinTestDsl

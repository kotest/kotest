package io.kotest.core.specs

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@DslMarker
annotation class KotestDsl

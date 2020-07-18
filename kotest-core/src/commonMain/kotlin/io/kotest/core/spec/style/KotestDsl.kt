package io.kotest.core.spec.style

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@DslMarker
annotation class KotestDsl

package io.kotest.core.annotation

import io.kotest.core.Platform

/**
 * Attach to a [io.kotest.core.spec.Spec], and that spec will only be instantiated when
 * running tests on the specified Platform(s)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPlatform(vararg val values: Platform)

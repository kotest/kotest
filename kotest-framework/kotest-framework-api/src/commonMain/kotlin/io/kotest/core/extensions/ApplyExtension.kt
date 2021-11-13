package io.kotest.core.extensions

import kotlin.reflect.KClass

/**
 * Use this annotation to register extensions on specs.
 * For example
 *
 * ```
 * @ApplyExtension(SpringExtensionFactory::class)
 * class MySpringSpec : FunSpec() { ... }
 * ```
 *
 * Note: This annotation will only be processed once per spec class, regardless of
 * how many times the class is instantiated.
 */
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ApplyExtension(vararg val extensions: KClass<out Extension>)

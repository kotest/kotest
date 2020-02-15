package io.kotest.core.engine

import io.kotest.core.config.Project
import io.kotest.core.spec.Spec
import io.kotest.fp.Try
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Creates an instance of a [Spec] by delegating to constructor extensions, with
 * a fallback to a reflection based zero-args constructor.
 */
fun <T : Spec> instantiateSpec(clazz: KClass<T>): Try<Spec> = Try {
   val nullSpec: Spec? = null
   Project.constructorExtensions()
      .fold(nullSpec) { spec, ext -> spec ?: ext.instantiate(clazz) } ?: clazz.createInstance()
}

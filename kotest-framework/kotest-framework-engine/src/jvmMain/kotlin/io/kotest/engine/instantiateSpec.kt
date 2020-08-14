package io.kotest.engine

import io.kotest.engine.config.Project
import io.kotest.core.spec.Spec
import io.kotest.fp.Try
import kotlin.reflect.KClass

/**
 * Creates an instance of a [AbstractSpec] by delegating to constructor extensions, with
 * a fallback to a reflection based zero-args constructor.
 */
fun <T : Spec> instantiateSpec(clazz: KClass<T>): Try<Spec> =
   Try {
      val nullSpec: Spec? = null
      Project.constructorExtensions()
         .fold(nullSpec) { spec, ext -> spec ?: ext.instantiate(clazz) } ?: javaReflectNewInstance(clazz)
   }

fun <T : Spec> javaReflectNewInstance(clazz: KClass<T>): Spec {
   val constructor = clazz.java.constructors[0]
   constructor.isAccessible = true
   return constructor.newInstance() as Spec
}

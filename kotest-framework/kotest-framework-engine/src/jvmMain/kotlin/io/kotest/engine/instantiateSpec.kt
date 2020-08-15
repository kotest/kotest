package io.kotest.engine

import io.kotest.core.config.configuration
import io.kotest.core.extensions.ConstructorExtension
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
      configuration.extensions().filterIsInstance<ConstructorExtension>()
         .fold(nullSpec) { spec, ext -> spec ?: ext.instantiate(clazz) } ?: javaReflectNewInstance(clazz)
   }

fun <T : Spec> javaReflectNewInstance(clazz: KClass<T>): Spec {
   val constructor = clazz.java.constructors[0]
   constructor.isAccessible = true
   return constructor.newInstance() as Spec
}

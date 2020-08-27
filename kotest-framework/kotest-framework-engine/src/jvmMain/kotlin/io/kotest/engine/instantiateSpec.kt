package io.kotest.engine

import io.kotest.core.config.configuration
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.spec.Spec
import io.kotest.fp.Try
import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

/**
 * Creates an instance of a [Spec] by delegating to constructor extensions, with
 * a fallback to a reflection based zero-args constructor.
 */
fun <T : Spec> instantiateSpec(clazz: KClass<T>): Try<Spec> =
   Try {
      val nullSpec: Spec? = null
      configuration.extensions().filterIsInstance<ConstructorExtension>()
         .fold(nullSpec) { spec, ext -> spec ?: ext.instantiate(clazz) } ?: javaReflectNewInstance(clazz)
   }

fun <T : Spec> javaReflectNewInstance(clazz: KClass<T>): Spec {
   try {
      val constructor = clazz.constructors.find { it.parameters.isEmpty() }
         ?: throw SpecInstantiationException("Could not create instance of $clazz. Specs must have a public zero-arg constructor.",
            null)
      constructor.isAccessible = true
      return constructor.call()
   } catch (t: Throwable) {
      throw SpecInstantiationException("Could not create instance of $clazz", t)
   }
}

class SpecInstantiationException(msg: String, t: Throwable?) : RuntimeException(msg, t)

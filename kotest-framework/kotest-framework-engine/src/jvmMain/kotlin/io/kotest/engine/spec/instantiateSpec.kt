package io.kotest.engine.spec

import io.kotest.core.SpecInstantiationException
import io.kotest.core.config.configuration
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.PostInstantiationExtension
import io.kotest.core.spec.Spec
import io.kotest.fp.Try
import io.kotest.fp.success
import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

/**
 * Creates an instance of a [Spec] by delegating to constructor extensions, with
 * a fallback to a reflection based zero-args constructor.
 *
 * If this clazz represents an object, then the singleton object instance will be returned
 *
 * After creation will execute any [PostInstantiationExtension]s.
 */
fun <T : Spec> createAndInitializeSpec(clazz: KClass<T>): Try<Spec> {
   return when (val obj = clazz.objectInstance) {
      null -> Try {
         val initial: Spec? = null
         val spec = configuration.extensions().filterIsInstance<ConstructorExtension>()
            .fold(initial) { spec, ext -> spec ?: ext.instantiate(clazz) } ?: javaReflectNewInstance(clazz)
         configuration.extensions().filterIsInstance<PostInstantiationExtension>()
            .fold(spec) { acc, ext -> ext.process(acc) }
      }
      else -> obj.success()
   }
}

internal fun <T : Spec> javaReflectNewInstance(clazz: KClass<T>): Spec {
   try {
      val constructor = clazz.constructors.find { it.parameters.isEmpty() }
         ?: throw SpecInstantiationException(
            "Could not create instance of $clazz. Specs must have a public zero-arg constructor.",
            null
         )
      constructor.isAccessible = true
      return constructor.call()
   } catch (t: Throwable) {
      throw SpecInstantiationException("Could not create instance of $clazz", t)
   }
}


package io.kotest.engine.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.PostInstantiationExtension
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

internal actual suspend fun instantiate(kclass: KClass<out Spec>): Result<Spec> {
   return createAndInitializeSpec(kclass)
}

/**
 * Creates an instance of a [Spec] by delegating to [ConstructorExtension], with
 * a fallback to a reflection based zero-args constructor.
 *
 * If the [kclass] represents an object, then the singleton object instance will be returned.
 *
 * After creation any [PostInstantiationExtension]s will be invoked.
 */
suspend fun <T : Spec> createAndInitializeSpec(kclass: KClass<T>): Result<Spec> {
   return when (val obj = kclass.objectInstance) {
      null -> runCatching {
         val initial: Spec? = null
         val spec = configuration.extensions().filterIsInstance<ConstructorExtension>()
            .fold(initial) { spec, ext -> spec ?: ext.instantiate(kclass) } ?: javaReflectNewInstance(kclass)
         configuration.extensions().filterIsInstance<PostInstantiationExtension>()
            .fold(spec) { acc, ext -> ext.instantiated(acc) }
      }
      else -> Result.success(obj)
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
      t.printStackTrace()
      throw SpecInstantiationException("Could not create instance of $clazz", t)
   }
}

class SpecInstantiationException(msg: String, t: Throwable?) : RuntimeException(msg, t)

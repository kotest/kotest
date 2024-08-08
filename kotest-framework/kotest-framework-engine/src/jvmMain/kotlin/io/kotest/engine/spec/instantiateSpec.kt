package io.kotest.engine.spec

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.PostInstantiationExtension
import io.kotest.core.spec.Spec
import io.kotest.mpp.annotation
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible

internal actual suspend fun instantiate(kclass: KClass<*>, registry: ExtensionRegistry): Result<Spec> {
   return createAndInitializeSpec(kclass as KClass<out Spec>, registry)
}

/**
 * Creates an instance of a [Spec] by delegating to a [ConstructorExtension], with
 * a fallback to a reflection based zero-args constructor.
 *
 * If the [kclass] represents an object, then the singleton object instance will be returned.
 *
 * After creation any [PostInstantiationExtension]s will be invoked.
 */
suspend fun <T : Spec> createAndInitializeSpec(kclass: KClass<T>, registry: ExtensionRegistry): Result<Spec> {
   return when (val obj = kclass.objectInstance) {
      null -> runCatching {
         val initial: Spec? = null

         val spec = constructorExtensions(registry, kclass)
            .fold(initial) { spec, ext -> spec ?: ext.instantiate(kclass) } ?: javaReflectNewInstance(kclass)

         postInstantiationExtensions(registry, kclass)
            .fold(spec) { acc, ext -> ext.instantiated(acc) }
      }

      else -> Result.success(obj)
   }.onSuccess { spec ->
      spec.globalExtensions().forEach { registry.add(it) }
   }
}

internal fun constructorExtensions(
   registry: ExtensionRegistry,
   kclass: KClass<*>
): List<ConstructorExtension> {
   return registry.all().filterIsInstance<ConstructorExtension>() +
      (kclass.annotation<ApplyExtension>()?.extensions
         ?.filter { it.isSubclassOf(ConstructorExtension::class) }
         ?.map { it.createInstance() as ConstructorExtension } ?: emptyList())
}

internal fun postInstantiationExtensions(
   registry: ExtensionRegistry,
   kclass: KClass<*>
): List<PostInstantiationExtension> {
   return registry.all().filterIsInstance<PostInstantiationExtension>() +
      (kclass.annotation<ApplyExtension>()?.extensions
         ?.filter { it.isSubclassOf(PostInstantiationExtension::class) }
         ?.map { it.createInstance() as PostInstantiationExtension } ?: emptyList())
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

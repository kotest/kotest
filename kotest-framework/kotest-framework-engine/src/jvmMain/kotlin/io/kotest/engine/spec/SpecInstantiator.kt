package io.kotest.engine.spec

import io.kotest.common.KotestInternal
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.PostInstantiationExtension
import io.kotest.core.spec.Spec
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.instantiateOrObject
import io.kotest.engine.mapError
import kotlin.reflect.KClass

/**
 * Creates an instance of a [Spec].
 *
 * Firstly, by delegating to any [ConstructorExtension]s then with
 * a fallback to reflection based zero-args constructor.
 *
 * If the reference is an object definition, then that singleton object instance will be returned.
 *
 * After instantiation any [PostInstantiationExtension]s will be invoked.
 */
@KotestInternal
class SpecInstantiator(
   private val registry: ExtensionRegistry,
   private val projectConfigResolver: ProjectConfigResolver
) {

   suspend fun <T : Spec> createAndInitializeSpec(
      kclass: KClass<T>,
   ): Result<Spec> {
      return objectOrInstantiateClass(kclass)
   }

   private suspend fun objectOrInstantiateClass(kclass: KClass<out Spec>): Result<Spec> {
      val obj = kclass.objectInstance
      return if (obj != null) Result.success(obj) else instantiate(kclass)
   }

   private suspend fun instantiate(kclass: KClass<out Spec>): Result<Spec> {
      return runCatching {
         val initial: Spec? = null

         val constructorExtensions = constructorExtensions(kclass)
         val spec = constructorExtensions
            .fold(initial) { spec, ext -> spec ?: ext.instantiate(kclass) }
            ?: instantiateOrObject(kclass)
               .mapError { SpecInstantiationException("Could not create instance of $kclass", it) }
               .getOrThrow()

         // any spec level AfterProjectListener extensions should now be added
         spec.projectExtensions().forEach { registry.add(it) }

         postInstantiationExtensions(kclass)
            .fold(spec) { acc, ext -> ext.instantiated(acc) }
      }
   }

   private fun constructorExtensions(
      kclass: KClass<*>
   ): List<ConstructorExtension> {
      return projectConfigResolver.extensionsOf<ConstructorExtension>() +
         registry.get(kclass).filterIsInstance<ConstructorExtension>()
   }

   private fun postInstantiationExtensions(
      kclass: KClass<*>
   ): List<PostInstantiationExtension> {
      return projectConfigResolver.extensionsOf<PostInstantiationExtension>() +
         registry.get(kclass).filterIsInstance<PostInstantiationExtension>()
   }
}

class SpecInstantiationException(msg: String, t: Throwable?) : RuntimeException(msg, t)

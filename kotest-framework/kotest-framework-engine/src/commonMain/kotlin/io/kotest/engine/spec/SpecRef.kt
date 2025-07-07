package io.kotest.engine.spec

import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.flatMap
import kotlin.reflect.KClass

/**
 * Returns instances of the [Spec] represented by this [SpecRef], handling lifecycle extension points,
 * and sealing the spec if it is a [DslDrivenSpec].
 */
internal class SpecRefInflator(
   private val registry: ExtensionRegistry,
   private val projectConfigRegistry: ProjectConfigResolver,
   private val extensions: SpecExtensions,
) {

   /**
    * Creates an instance of the given [SpecRef], notifies users of the instantiation event
    * or instantiation failure, and returns a Result with the error or spec.
    *
    * After this method is called the spec is sealed so no further configuration or root tests can be added.
    */
   internal suspend fun inflate(ref: SpecRef): Result<Spec> {
      val instance = when (ref) {
         is SpecRef.Reference -> instantiate(ref.kclass, registry, projectConfigRegistry)
         is SpecRef.Function -> Result.success(ref.f())
      }
      return instance
         .onFailure { extensions.specInstantiationError(ref.kclass, it) }
         .flatMap { spec -> extensions.specInstantiated(spec).map { spec } }
         .onSuccess { if (it is DslDrivenSpec) it.seal() }
   }
}

/**
 * Instantiates a [Spec] reflectively from the given [KClass].
 */
internal expect suspend fun instantiate(
   kclass: KClass<*>,
   registry: ExtensionRegistry,
   projectConfigRegistry: ProjectConfigResolver
): Result<Spec>

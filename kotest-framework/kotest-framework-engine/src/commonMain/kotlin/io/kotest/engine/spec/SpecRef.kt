package io.kotest.engine.spec

import io.kotest.core.spec.AbstractSpec
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.flatMap
import kotlin.reflect.KClass

/**
 * Returns instances of the [Spec] represented by this [SpecRef], handling lifecycle extension points,
 * and sealing the spec if it is a [AbstractSpec].
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
    * After this method is invoked, the spec is sealed, so no further configuration or root tests can be added.
    */
   internal suspend fun inflate(ref: SpecRef): Result<Spec> {
      val instance = when (ref) {
         is SpecRef.Reference -> instantiate(ref.kclass, registry, projectConfigRegistry)
         is SpecRef.Function -> Result.success(ref.f())
      }
      return instance
         .onFailure { extensions.specInstantiationError(ref.kclass, it) }
         .flatMap { spec -> extensions.specInstantiated(spec).map { spec } }
         .onSuccess { spec ->
            // Any spec level AfterProjectListener extensions should now be added to the global registry.
            // The registry is engine-wide and add() does not dedupe, so under InstancePerRoot/
            // InstancePerLeaf/InstancePerTest isolation - where inflate() runs once per fresh spec
            // instance and each instance builds new afterProject listener objects - we would otherwise
            // register (and therefore run) a spec-body afterProject{} block once per instance instead of
            // once per project. We dedupe by spec class so these listeners are registered at most once,
            // regardless of how many instances are created (and exactly once for SingleInstance mode).
            if (registry.markAfterProjectListenersRegistered(ref.kclass)) {
               spec.afterProjectListeners().forEach { registry.add(it) }
            }
            // seal the spec to detect adding root tests after execution has started
            if (spec is AbstractSpec) {
               spec.sealed = true
            }
         }
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

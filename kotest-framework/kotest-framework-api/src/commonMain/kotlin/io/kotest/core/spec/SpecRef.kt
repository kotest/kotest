package io.kotest.core.spec

import io.kotest.core.config.ExtensionRegistry
import kotlin.reflect.KClass

/**
 * A [SpecRef] is a reference to a spec that was detected during scans or compilation.
 *
 * Each ref contains a reference to the KClass of that spec and contains a function
 * to retrieve an instance of the [Spec].
 */
interface SpecRef {

   /**
    * The KClass for the spec that this [SpecRef] references.
    */
   val kclass: KClass<out Spec>

   /**
    * Returns an instance of the spec that this [SpecRef] references.
    *
    * May return an error if an instance could not be created (eg, on JVM, instances are created
    * refectively, and this may error if the constructor is not known).
    *
    * @param registry the extension's registry, used to lookup extensions for the instantiation process.
    */
   suspend fun instance(registry: ExtensionRegistry): Result<Spec>
}

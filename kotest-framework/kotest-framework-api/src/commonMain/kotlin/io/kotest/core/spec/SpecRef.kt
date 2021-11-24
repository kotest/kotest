package io.kotest.core.spec

import kotlin.reflect.KClass

/**
 * A [SpecRef] is a reference to a spec that was detected during classpath
 * scanning or during compilation.
 *
 * On platforms that lack reflective capability, such as nodeJS, web or kotlin/native,
 * specs are preconstructed or constructed through a simple function. On the JVM, the
 * powerful reflection support means instances can be created via the [KClass] reference.
 */
sealed interface SpecRef {

   /**
    * The KClass for the spec that this [SpecRef] references.
    */
   val kclass: KClass<out Spec>

   /**
    * A [SpecRef] that contains only a [kclass] reference and instances
    * must be created using reflection.
    */
   data class Reference(override val kclass: KClass<out Spec>) : SpecRef

   /**
    * A [SpecRef] that contains a singleton spec [instance].
    */
   data class Singleton(val instance: Spec) : SpecRef {
      override val kclass: KClass<out Spec> = instance::class
   }

   /**
    * A [SpecRef] that contains a function that can be invoked to construct a spec.
    */
   data class Function(val f: () -> Spec, override val kclass: KClass<out Spec>) : SpecRef
}

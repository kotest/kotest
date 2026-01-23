package io.kotest.core.spec

import io.kotest.common.KotestInternal
import io.kotest.common.KotestTesting
import io.kotest.common.reflection.bestName
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import kotlin.reflect.KClass

/**
 * A [SpecRef] is a reference to a spec that was detected during classpath
 * scanning or during compilation.
 *
 * On platforms that lack reflective capability, such as Node.js, web or Kotlin/Native,
 * specs are pre-constructed or constructed through a simple function. On JVM, the
 * powerful reflection support means instances can be created via the [KClass] reference.
 */
sealed interface SpecRef {

   /**
    * The KClass for the spec that this [SpecRef] references.
    * On non-JVM the KClass has much less utility as reflection is not supported.
    */
   val kclass: KClass<out Spec>

   /**
    * The fully qualified name of the spec.
    */
   val fqn: String

   /**
    * A [SpecRef] that contains only a [kclass] reference and instances are created using reflection.
    * This allows the engine to instantiate specs with non-empty constructors, eg for dependency injection.
    */
   data class Reference(
      override val kclass: KClass<out Spec>,
      override val fqn: String
   ) : SpecRef {

      @KotestTesting
      @KotestInternal
      constructor(kclass: KClass<out Spec>) : this(kclass, kclass.bestName())
   }

   /**
    * A [SpecRef] that contains a function that can be invoked to construct a spec.
    * This is required for platforms that do not support reflection, such as Kotlin/JS or Kotlin/Native.
    * A function ref only supports specs that have no constructor parameters.
    */
   data class Function(
      val f: () -> Spec,
      override val kclass: KClass<out Spec>,
      override val fqn: String,
   ) : SpecRef {

      @KotestTesting
      @KotestInternal
      constructor(f: () -> Spec, kclass: KClass<out Spec>) : this(f, kclass, kclass.bestName())
   }
}

@KotestInternal
fun SpecRef.descriptor() = Descriptor.SpecDescriptor(DescriptorId(fqn))

fun SpecRef.name() = when (this) {
   is SpecRef.Reference -> kclass.simpleName ?: "UnknownSpec"
   is SpecRef.Function -> kclass.simpleName ?: "UnknownSpec"
}

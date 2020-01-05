package io.kotest.core.specs

import io.kotest.core.fp.Option
import io.kotest.core.fp.getOrElse
import io.kotest.core.fp.orElse
import io.kotest.core.fp.toOption
import kotlin.reflect.KClass

sealed class SpecContainer {

   abstract val qualifiedName: Option<String>

   abstract val simpleName: Option<String>

   val name = lazy { qualifiedName.orElse(simpleName).getOrElse("<unknown") }

   data class ValueSpec(
      val spec: Lazy<Spec>,
      val packageName: String,
      override val qualifiedName: Option<String>,
      override val simpleName: Option<String>,
      // the file that contains the val
      val path: String
   ) :
      SpecContainer()

   data class ClassSpec(val kclass: KClass<out SpecBuilder>) : SpecContainer() {
      override val qualifiedName: Option<String> = kclass.fqn()
      override val simpleName: Option<String> = kclass.simpleName.toOption()
   }
}

expect fun KClass<*>.fqn(): Option<String>

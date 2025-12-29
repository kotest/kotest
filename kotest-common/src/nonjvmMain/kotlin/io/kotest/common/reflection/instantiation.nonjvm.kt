package io.kotest.common.reflection

import io.kotest.common.platform
import kotlin.reflect.KClass

actual val instantiations: Instantiations = NoInstantiations

object NoInstantiations : Instantiations {
   override fun <T : Any> newInstanceNoArgConstructorOrObjectInstance(kclass: KClass<T>): T {
      error("Not available on platform: $platform")
   }
}

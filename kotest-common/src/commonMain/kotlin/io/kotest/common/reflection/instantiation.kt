package io.kotest.common.reflection

import kotlin.reflect.KClass

expect val instantiations: Instantiations

interface Instantiations {
   fun <T : Any> newInstanceNoArgConstructorOrObjectInstance(kclass: KClass<T>): T
}

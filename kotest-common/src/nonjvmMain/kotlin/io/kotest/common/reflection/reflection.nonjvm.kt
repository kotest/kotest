package io.kotest.common.reflection

import kotlin.reflect.KClass

actual val reflection: Reflection = BasicReflection

object BasicReflection : Reflection {
   override fun fqn(kclass: KClass<*>): String? = null
   override fun annotations(kclass: KClass<*>, parameters: Set<AnnotationSearchParameter>): List<Annotation> =
      emptyList()

   override fun <T : Any> isDataClass(kclass: KClass<T>): Boolean = false
   override fun <T : Any> isEnumClass(kclass: KClass<T>): Boolean = false
   override fun <T : Any> primaryConstructorMembers(klass: KClass<T>): List<Property> = emptyList()
}

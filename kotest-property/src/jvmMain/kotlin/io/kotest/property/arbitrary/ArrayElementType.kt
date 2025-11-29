package io.kotest.property.arbitrary

import kotlin.reflect.KClass
import kotlin.reflect.KType

internal fun arrayElementType(type: KType) : KClass<*>? {
   val clazz = type.classifier as? KClass<*> ?: return null
   if(clazz.java.isArray) {
      val componentType = clazz.java.componentType
      if (componentType.isPrimitive) {
         return componentType.kotlin
      } else {
         val elementType = type.arguments.firstOrNull()?.type ?: return null
         return elementType.classifier as? KClass<*>
      }
   }
   return null
}

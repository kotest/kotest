package io.kotest.property.arbitrary

import kotlin.reflect.KClass
import kotlin.reflect.KType

fun arrayElementType(type: KType) : KClass<*>? {
   val clazz = type.classifier as? KClass<*> ?: return null
   if(clazz.java.isArray) {
      val componentType = clazz.java.componentType
      if (componentType.isPrimitive) {
         return componentType.kotlin
      } else {
         val elementType = type.arguments.first().type ?: return null
         return elementType.classifier as? KClass<*>
      }
   }
   return null
}

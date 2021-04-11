package io.kotest.mpp

import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Returns true if the given type is a class and considered "stable".
 */
fun isStable(type: KType) = when (val classifier = type.classifier) {
   is KClass<*> -> isStable(classifier)
   else -> false
}

/**
 * Returns true if the given class is a "stable" class.
 *
 * A stable class is either:
 *  - a primitive type or a String
 *  - an enum class
 *  - a data class that only contains stable classes as members
 */
fun isStable(kclass: KClass<*>): Boolean {

   val primitiveTypes = setOf(
      String::class,
      Int::class,
      Long::class,
      Double::class,
      Float::class,
      Byte::class,
      Short::class,
      Boolean::class
   )

   /**
    * Returns true if all members of this class are "stable".
    */
   fun hasStableMembers(kclass: KClass<*>) = reflection.primaryConstructorMembers(kclass).let { members ->
      members.isNotEmpty() && members.all { isStable(it.type) }
   }

   return when {
      primitiveTypes.contains(kclass) -> true
      reflection.isEnumClass(kclass) || (reflection.isDataClass(kclass) && hasStableMembers(kclass)) -> true
      else -> false
   }
}




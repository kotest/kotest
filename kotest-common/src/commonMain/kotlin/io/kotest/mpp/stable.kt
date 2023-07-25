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
 * Returns true if the given class is a "stable" class, ie one that has a consistent toString().
 *
 * A stable class is either:
 *  - a Kotlin or Java primitive type or a String
 *  - an enum class
 *  - a data class that only contains stable classes as members
 *  - a stable type on the executing platform
 */
fun isStable(kclass: KClass<*>): Boolean {
   return when {
      allPlatformStableTypes.contains(kclass) -> true
      reflection.isEnumClass(kclass) -> true
      reflection.isDataClass(kclass) && hasStableMembers(kclass) -> true
      isPlatformStable(kclass) -> true
      else -> {
         println("Warning, type $kclass used in data testing does not have a stable toString()")
         false
      }
   }
}

expect fun isPlatformStable(kclass: KClass<*>): Boolean

/**
 * Returns true if all members of this class are "stable".
 */
private fun hasStableMembers(kclass: KClass<*>) = reflection.primaryConstructorMembers(kclass).let { members ->
   members.isNotEmpty() && members.all { isStable(it.type) }
}

private val allPlatformStableTypes = setOf(
   String::class,
   Int::class,
   Long::class,
   Double::class,
   Float::class,
   Byte::class,
   Short::class,
   Boolean::class,
   Pair::class,
   Triple::class,
   Char::class,
)

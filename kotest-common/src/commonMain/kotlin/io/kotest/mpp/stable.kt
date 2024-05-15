package io.kotest.mpp

import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Returns true if the given type is a class and considered "stable".
 */
@Deprecated("Internal function, will not be exposed in the future. Deprecated since 5.9")
fun isStable(type: KType) = when (val classifier = type.classifier) {
   is KClass<*> -> isStable(classifier, null)
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
@Deprecated("Internal function, will not be exposed in the future. Deprecated since 5.9")
fun isStable(kclass: KClass<*>) = isStable(kclass, null)

/**
 * Returns true if the given class is a "stable" class, ie one that has a consistent toString().
 *
 * A stable class is either:
 *  - a Kotlin or Java primitive type or a String
 *  - an enum class
 *  - a data class that only contains stable classes as members
 *  - a stable type on the executing platform
 */
@Deprecated("Internal function, will not be exposed in the future. Deprecated since 5.9")
fun isStable(kclass: KClass<*>, t: Any? = null): Boolean {
   return when {
      allPlatformStableTypes.contains(kclass) -> true
      reflection.isEnumClass(kclass) -> true
      reflection.isDataClass(kclass) && hasStableMembers(kclass, t) -> true
      isPlatformStable(kclass) -> true
      else -> {
         println("Warning, type $kclass used in data testing does not have a stable toString()")
         false
      }
   }
}

@Deprecated("Internal function, will not be exposed in the future. Deprecated since 5.9")
expect fun isPlatformStable(kclass: KClass<*>): Boolean

/**
 * Returns true if all members of this class are "stable".
 */
private fun hasStableMembers(kclass: KClass<*>, t: Any? = null) =
   reflection.primaryConstructorMembers(kclass).let { members ->
      members.isNotEmpty() && members.all { getter ->
         val typeIsStable = isStable(getter.type)
         if (t == null) {
            typeIsStable
         } else {
            val valueIsStable = getter.call(t)?.let { memberValue ->
               isStable(memberValue::class, memberValue)
            } ?: false

            typeIsStable || valueIsStable
         }
      }
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
   Char::class,
)

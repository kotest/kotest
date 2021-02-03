package io.kotest.core.datatest

import io.kotest.mpp.bestName
import io.kotest.mpp.reflection
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class Identifiers() {

   private var names = mutableListOf<String>()

   /**
    * Each test name must be unique. We can use the toString if we determine the instance is stable.
    *
    * An instance is considered stable if it is a data class where each parameter is either a data class itself,
    * or one of the [primitiveTypes].
    *
    * Note: If the user has overridden toString() and the returned value is not stable, tests may not appear.
    */
   fun stableIdentifier(t: Any): String {
      val name = if (isStable(t::class)) {
         t.toString()
      } else {
         t::class.bestName()
      }
      val count = names.count { it == name }
      names.add(name)
      return if (count == 0) name else "$name ($count)"
   }
}

internal fun isStable(type: KType) = when (val classifier = type.classifier) {
   is KClass<*> -> isStable(classifier)
   else -> false
}

internal val primitiveTypes = setOf(
   String::class,
   Int::class,
   Long::class,
   Double::class,
   Float::class,
   Byte::class,
   Short::class,
   Boolean::class
)

internal fun KClass<*>.hasStableMembers() = reflection.primaryConstructorMembers(this).let { members ->
   members.isNotEmpty() && members.all { isStable(it.type) }
}

internal fun isStable(type: KClass<*>): Boolean {
   return when {
      primitiveTypes.contains(type) -> true
      (reflection.isEnumClass(type) || reflection.isDataClass(type)) && type.hasStableMembers() -> true
      else -> false
   }
}

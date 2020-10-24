package io.kotest.core.datatest

import io.kotest.mpp.bestName
import io.kotest.mpp.reflection
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Each test name must be unique. We can use the toString if we determine the instance is stable.
 *
 * An instance is considered stable if it is a data class where each parameter is either a data class itself,
 * or a primitive type.
 *
 * Note: If the user has overridden toString() and the returned value is not stable, tests may not appear.
 */
internal fun stableIdentifier(t: Any, counter: Int): String {
   return if (isStable(t::class)) {
      t.toString()
   } else {
      t::class.bestName() + " $counter"
   }
}

internal fun isStable(type: KType) = when (val classifier = type.classifier) {
   is KClass<*> -> isStable(classifier)
   else -> false
}

internal fun isStable(type: KClass<*>): Boolean {
   return when (type) {
      String::class -> true
      Int::class -> true
      Long::class -> true
      Double::class -> true
      Float::class -> true
      Byte::class -> true
      Short::class -> true
      Boolean::class -> true
      else -> reflection.isDataClass(type) &&
         reflection.primaryConstructorMembers(type).let { members ->
            members.isNotEmpty() && members.all { isStable(it.type) }
         }
   }
}

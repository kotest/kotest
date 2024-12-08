package io.kotest.engine.stable

import io.kotest.mpp.bestName
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * This method will return the stable value if the type is considered stable for all platforms,
 * or null if not a stable type.
 */
@OptIn(ExperimentalUuidApi::class)
fun platformStableValue(value: Any?): String? {
   return when (value) {
      is String -> value
      is Int -> value.toString()
      is Long -> value.toString()
      is Double -> value.toString()
      is Float -> value.toString()
      is Byte -> value.toString()
      is Short -> value.toString()
      is Boolean -> value.toString()
      is Char -> value.toString()
      is Duration -> value.toString()
      is Uuid -> value.toString()
      is UByte -> value.toString()
      is UShort -> value.toString()
      is UInt -> value.toString()
      is ULong -> value.toString()
      is KClass<*> -> value.qualifiedName ?: value.bestName()
      is Regex -> value.toString()
      is BooleanArray -> value.joinToString(",")
      is ByteArray -> value.joinToString(",")
      is CharArray -> value.joinToString("")
      is ShortArray -> value.joinToString(",")
      is IntArray -> value.joinToString(",")
      is LongArray -> value.joinToString(",")
      is FloatArray -> value.joinToString(",")
      is DoubleArray -> value.joinToString(",")
      is Unit -> "Unit"
      else -> null
   }
}

@OptIn(ExperimentalUuidApi::class)
fun isAllPlatformStable(kclass: KClass<*>): Boolean {
   return when (kclass) {
      String::class -> true
      Int::class -> true
      Long::class -> true
      Double::class -> true
      Float::class -> true
      Byte::class -> true
      Short::class -> true
      Boolean::class -> true
      Char::class -> true
      Duration::class -> true
      UByte::class -> true
      UShort::class -> true
      UInt::class -> true
      ULong::class -> true
      Uuid::class -> true
      Regex::class -> true
      BooleanArray::class -> true
      ByteArray::class -> true
      CharArray::class -> true
      ShortArray::class -> true
      IntArray::class -> true
      LongArray::class -> true
      FloatArray::class -> true
      DoubleArray::class -> true
      Unit::class -> true
      KClass::class -> true
      else -> false
   }
}

/**
 * Returns true if the given [KClass] is a type considered a stable type for that platform.
 * Eg, the JDK Path object is considered stable.
 */
internal expect fun isPlatformStable(kclass: KClass<*>): Boolean

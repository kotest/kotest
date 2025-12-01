package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.arrayElementType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.typeOf

class ArrayElementTypeTest : StringSpec() {
   init {
      "BooleanArray element type should be Boolean" {
         val type: KType = typeOf<BooleanArray>()
         val elementType: KClass<*>? = arrayElementType(type)
         elementType shouldBe Boolean::class
      }
       "ByteArray element type should be Byte" {
          val thing = ByteArray(10)
          val type: KType = thing::class.java.kotlin.createType()
          val elementType: KClass<*>? = arrayElementType(type)
          elementType shouldBe Byte::class
       }
      "ShortArray element type should be Int" {
         val type: KType = typeOf<ShortArray>()
         val elementType: KClass<*>? = arrayElementType(type)
         elementType shouldBe Short::class
      }
      "IntArray element type should be Int" {
         val thing = IntArray(10)
         val type: KType = thing::class.java.kotlin.createType()
         val elementType: KClass<*>? = arrayElementType(type)
         elementType shouldBe Int::class
      }
      "LongArray element type should be Long" {
         val type: KType = typeOf<LongArray>()
         val elementType: KClass<*>? = arrayElementType(type)
         elementType shouldBe Long::class
      }
      "FloatArray element type should be Float" {
         val type: KType = typeOf<FloatArray>()
         val elementType: KClass<*>? = arrayElementType(type)
         elementType shouldBe Float::class
      }
      "DoubleArray element type should be Float" {
         val type: KType = typeOf<DoubleArray>()
         val elementType: KClass<*>? = arrayElementType(type)
         elementType shouldBe Double::class
      }
      "Array<String> element type should be String" {
         val type: KType = typeOf<Array<String>>()
         val elementType: KClass<*>? = arrayElementType(type)
         elementType shouldBe String::class
      }
      "arrayElementType should return type of element" {
         val type: KType = typeOf<Array<Thing>>()
         val elementType: KClass<*>? = arrayElementType(type)
         elementType shouldBe Thing::class
      }
      "arrayElementType should return null for non-array types" {
         val type: KType = typeOf<Thing>()
         val elementType: KClass<*>? = arrayElementType(type)
         elementType shouldBe null
      }
   }
}
data class Thing(val name: String)


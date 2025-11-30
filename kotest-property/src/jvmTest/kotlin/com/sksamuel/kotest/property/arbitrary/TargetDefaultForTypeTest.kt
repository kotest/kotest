package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.targetDefaultForType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

class TargetDefaultForTypeTest : StringSpec() {
   init {
      "works for ByteArray" {
         val arb = targetDefaultForType(
            providedArbs = mapOf(),
            arbsForProps = mapOf(),
            type = typeOf<ByteArray>()
         )
         arb.shouldNotBeNull().sample(RandomSource.default()).value.shouldBeInstanceOf<ByteArray> {
            it.size.shouldBePositive()
         }
      }

      "works for ShortArray" {
         val arb = targetDefaultForType(
            providedArbs = mapOf(),
            arbsForProps = mapOf(),
            type = typeOf<ShortArray>()
         )
         arb.shouldNotBeNull().sample(RandomSource.default()).value.shouldBeInstanceOf<ShortArray> {
            it.size.shouldBePositive()
         }
      }

      "works for IntArray" {
         val arb = targetDefaultForType(
            providedArbs = mapOf(),
            arbsForProps = mapOf(),
            type = typeOf<IntArray>()
         )
         arb.shouldNotBeNull().sample(RandomSource.default()).value.shouldBeInstanceOf<IntArray> {
            it.size.shouldBePositive()
         }
      }

      "works for LongArray" {
         val arb = targetDefaultForType(
            providedArbs = mapOf(),
            arbsForProps = mapOf(),
            type = typeOf<LongArray>()
         )
         arb.shouldNotBeNull().sample(RandomSource.default()).value.shouldBeInstanceOf<LongArray> {
            it.size.shouldBePositive()
         }
      }

      "works for FloatArray" {
         val arb = targetDefaultForType(
            providedArbs = mapOf(),
            arbsForProps = mapOf(),
            type = typeOf<FloatArray>()
         )
         arb.shouldNotBeNull().sample(RandomSource.default()).value.shouldBeInstanceOf<FloatArray> {
            it.size.shouldBePositive()
         }
      }

      "works for DoubleArray" {
         val arb = targetDefaultForType(
            providedArbs = mapOf(),
            arbsForProps = mapOf(),
            type = typeOf<DoubleArray>()
         )
         arb.shouldNotBeNull().sample(RandomSource.default()).value.shouldBeInstanceOf<DoubleArray> {
            it.size.shouldBePositive()
         }
      }

      "works for BooleanArray" {
         val arb = targetDefaultForType(
            providedArbs = mapOf(),
            arbsForProps = mapOf(),
            type = typeOf<BooleanArray>()
         )
         arb.shouldNotBeNull().sample(RandomSource.default()).value.shouldBeInstanceOf<BooleanArray> {
            it.size.shouldBePositive()
         }
      }

      "works for CharArray" {
         val arb = targetDefaultForType(
            providedArbs = mapOf(),
            arbsForProps = mapOf(),
            type = typeOf<CharArray>()
         )
         arb.shouldNotBeNull().sample(RandomSource.default()).value.shouldBeInstanceOf<CharArray> {
            it.size.shouldBePositive()
         }
      }
   }
}

fun browseParameters(thing: Any) {
   val kclass: KClass<out Any> = thing::class
   val constructor = kclass.primaryConstructor

   constructor?.parameters?.map { param ->
      val type = param.type
      if(type == typeOf<IntArray>()) {
         println("It's an IntArray!")
      } else {
         println("Not an IntArray, it's $type")
      }
      val classifier = type.classifier
      println("Class: ${kclass.qualifiedName} Parameter: ${param.name}, type: $type, classifier: $classifier")
   }
}

   data class WithPrimitiveArray(val arr: IntArray)
   data class WithBoxedArray(val arr: Array<Int>)

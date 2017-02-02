package io.kotlintest.properties

import com.sksamuel.koors.Random
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

interface Gen<T> {
  fun generate(): T

  companion object {

    fun choose(min: Int, max: Int): Gen<Int> = object : Gen<Int> {
      override fun generate(): Int = Random.default.nextInt(max - min) + min
    }

    fun choose(min: Long, max: Long): Gen<Long> = object : Gen<Long> {
      override fun generate(): Long = (Random.default.nextLong() + min) % max
    }

    fun <T> oneOf(values: List<T>): Gen<T> = object : Gen<T> {
      override fun generate(): T = Random.default.shuffle(values).first()
    }

    fun string(): Gen<String> = object : Gen<String> {
      override fun generate(): String = nextPrintableString(Random.default.nextInt(100))
    }

    fun int() = object : Gen<Int> {
      override fun generate(): Int = Random.default.nextInt()
    }

    fun positiveIntegers() = nats()

    fun nats() = object : Gen<Int> {
      override fun generate(): Int {
        while (true) {
          val next = Random.default.nextInt()
          if (next >= 0)
            return next
        }
      }
    }

    fun negativeIntegers() = object : Gen<Int> {
      override fun generate(): Int {
        while (true) {
          val next = Random.default.nextInt()
          if (next < 0)
            return next
        }
      }
    }

    fun file() = object : Gen<File> {
      override fun generate(): File = File(string().generate())
    }

    fun long() = object : Gen<Long> {
      override fun generate(): Long = Random.default.nextLong()
    }

    fun bool() = object : Gen<Boolean> {
      override fun generate(): Boolean = Random.default.nextBoolean()
    }

    fun double() = object : Gen<Double> {
      override fun generate(): Double = Random.default.nextDouble()
    }

    fun float() = object : Gen<Float> {
      override fun generate(): Float = Random.default.nextFloat()
    }

    fun <T> create(fn: () -> T): Gen<T> = object : Gen<T> {
      override fun generate(): T = fn()
    }

    fun <T> set(gen: Gen<T>): Gen<Set<T>> = object : Gen<Set<T>> {
      override fun generate(): Set<T> = (0..Random.default.nextInt(100)).map { gen.generate() }.toSet()
    }

    fun <T> list(gen: Gen<T>): Gen<List<T>> = object : Gen<List<T>> {
      override fun generate(): List<T> = (0..Random.default.nextInt(100)).map { gen.generate() }.toList()
    }

    fun forClassName(className: String): Gen<*> {
      return when (className) {
        "java.lang.String" -> Gen.string()
        "kotlin.String" -> Gen.string()
        "java.lang.Integer" -> Gen.int()
        "kotlin.Int" -> Gen.int()
        "java.lang.Long" -> Gen.long()
        "kotlin.Long" -> Gen.long()
        "java.lang.Boolean" -> Gen.bool()
        "kotlin.Boolean" -> Gen.bool()
        "java.lang.Float" -> Gen.float()
        "kotlin.Float" -> Gen.float()
        "java.lang.Double" -> Gen.double()
        "kotlin.Double" -> Gen.double()
        else -> throw IllegalArgumentException("Cannot infer generator for $className; specify generators explicitly")
      }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> default(): Gen<T> {
      return when (T::class.qualifiedName) {
        List::class.qualifiedName -> {
          val type = object : TypeReference<T>() {}.type as ParameterizedType
          val first = type.actualTypeArguments.first() as WildcardType
          val upper = first.upperBounds.first() as Class<*>
          list(forClassName(upper.name)) as Gen<T>
        }
        Set::class.qualifiedName -> {
          val type = object : TypeReference<T>() {}.type as ParameterizedType
          val first = type.actualTypeArguments.first() as WildcardType
          val upper = first.upperBounds.first() as Class<*>
          set(forClassName(upper.name)) as Gen<T>
        }
        else -> forClassName(T::class.qualifiedName!!) as Gen<T>
      }
    }
  }

  fun nextPrintableString(length: Int): String {
    return (0..length).map { Random.Companion.default.nextPrintableChar() }.joinToString("")
  }
}

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
  val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
  override fun compareTo(other: TypeReference<T>) = 0
}
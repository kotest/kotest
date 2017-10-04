package io.kotlintest.properties

import io.kotlintest.JavaRandoms
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.Random

/** A shared random number generator. */
private val RANDOM = Random()

interface Gen<out T> {

  fun generate(): T

  companion object {

    fun choose(min: Int, max: Int): Gen<Int> {
      assert(min < max, { "min must be < max" })
      return object : Gen<Int> {
        override fun generate(): Int = JavaRandoms.internalNextInt(RANDOM, min, max)
      }
    }

    fun choose(min: Long, max: Long): Gen<Long> {
      assert(min < max, { "min must be < max" })
      return object : Gen<Long> {
        override fun generate(): Long = JavaRandoms.internalNextLong(RANDOM, min, max)
      }
    }

    fun <T> oneOf(vararg generators: Gen<T>): Gen<T> = object : Gen<T> {
      override fun generate(): T = Gen.oneOf(generators.toList()).generate().generate()
    }

    fun <T> oneOf(values: List<T>): Gen<T> = object : Gen<T> {
      override fun generate(): T = values[RANDOM.nextInt(values.size)]
    }

    inline fun <reified T : Enum<T>> oneOf() = oneOf(T::class.java.enumConstants.toList())

    fun string(): Gen<String> = object : Gen<String> {
      override fun generate(): String = nextPrintableString(RANDOM.nextInt(100))
    }

    fun int() = object : Gen<Int> {
      override fun generate(): Int = RANDOM.nextInt()
    }

    fun positiveIntegers() = nats()

    fun nats() = object : Gen<Int> {
      override fun generate(): Int {
        while (true) {
          val next = RANDOM.nextInt()
          if (next >= 0)
            return next
        }
      }
    }

    fun negativeIntegers() = object : Gen<Int> {
      override fun generate(): Int {
        while (true) {
          val next = RANDOM.nextInt()
          if (next < 0)
            return next
        }
      }
    }

    fun file() = object : Gen<File> {
      override fun generate(): File = File(string().generate())
    }

    fun long() = object : Gen<Long> {
      override fun generate(): Long = RANDOM.nextLong()
    }

    fun bool() = object : Gen<Boolean> {
      override fun generate(): Boolean = RANDOM.nextBoolean()
    }

    fun double() = object : Gen<Double> {
      override fun generate(): Double = RANDOM.nextDouble()
    }

    fun float() = object : Gen<Float> {
      override fun generate(): Float = RANDOM.nextFloat()
    }

    fun <T> create(fn: () -> T): Gen<T> = object : Gen<T> {
      override fun generate(): T = fn()
    }

    fun <T> set(gen: Gen<T>): Gen<Set<T>> = object : Gen<Set<T>> {
      override fun generate(): Set<T> = (0..RANDOM.nextInt(100)).map { gen.generate() }.toSet()
    }

    fun <T> list(gen: Gen<T>): Gen<List<T>> = object : Gen<List<T>> {
      override fun generate(): List<T> = (0..RANDOM.nextInt(100)).map { gen.generate() }.toList()
    }

    fun <K, V> pair(genK: Gen<K>, genV: Gen<V>): Gen<Pair<K, V>> = object : Gen<Pair<K, V>> {
      override fun generate(): Pair<K, V> = genK.generate() to genV.generate()
    }

    fun <K, V> map(genK: Gen<K>, genV: Gen<V>): Gen<Map<K, V>> = object : Gen<Map<K, V>>  {
      override fun generate(): Map<K, V> = Gen.list(pair(genK, genV)).generate().toMap()
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
        Pair::class.qualifiedName -> {
          val type = object : TypeReference<T>() {}.type as ParameterizedType
          val first = (type.actualTypeArguments[0] as WildcardType).upperBounds.first() as Class<*>
          val second = (type.actualTypeArguments[1] as WildcardType).upperBounds.first() as Class<*>
          pair(forClassName(first.name), forClassName(second.name)) as Gen<T>
        }
        Map::class.qualifiedName -> {
          val type = object : TypeReference<T>() {}.type as ParameterizedType
          //map key type can have or have not variance
          val first = if (type.actualTypeArguments[0] is Class<*>) {
            type.actualTypeArguments[0] as Class<*>
          } else {
            (type.actualTypeArguments[0] as WildcardType).upperBounds.first() as Class<*>
          }
          val second = (type.actualTypeArguments[1] as WildcardType).upperBounds.first() as Class<*>
          map(forClassName(first.name), forClassName(second.name)) as Gen<T>
        }
        else -> forClassName(T::class.qualifiedName!!) as Gen<T>
      }
    }
  }

  /**
   * Returns the next pseudorandom, uniformly distributed value
   * from the ASCII range 33-126.
   */
  private fun Random.nextPrintableChar(): Char {
    val low = 33
    val high = 127
    return (nextInt(high - low) + low).toChar()
  }

  fun nextPrintableString(length: Int): String {
    return (0 until length).map { RANDOM.nextPrintableChar() }.joinToString("")
  }
}

data class ConstGen<out T>(val value: T) : Gen<T> {
  override fun generate(): T =
      value
}

fun <T> Gen<T>.orNull(): Gen<T?> =
    Gen.oneOf(this, ConstGen(null))

internal tailrec fun <T> Gen<T>.generateGood(isGood: (T) -> Boolean): T =
    generate().takeIf(isGood) ?: generateGood(isGood)

fun <T> Gen<T>.filter(f: (T) -> Boolean): Gen<T> =
    Gen.create {
      generateGood(f)
    }

inline fun <A, B> Gen<A>.map(crossinline f: (A) -> B): Gen<B> =
    Gen.create {
      f(generate())
    }

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
  val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
  override fun compareTo(other: TypeReference<T>) = 0
}

package io.kotlintest.properties

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.*

/** A shared random number generator. */
private val RANDOM = Random()

interface Gen<out T> {
  fun generate(): T

  companion object {
    inline fun <T> create(crossinline fn: () -> T): Gen<T> = object : Gen<T> {
      override fun generate(): T = fn()
    }

    fun choose(min: Int, max: Int) = create { RANDOM.nextInt((max.toLong() - min.toLong()).toInt()) + min }
    fun choose(min: Long, max: Long) = create {
      var rand = (RANDOM.nextLong() % (max - min))
      if (rand < 0) {
        rand += max - min
      }
      rand + min
    }

    fun <T> oneOf(vararg generators: Gen<T>) = create { Gen.oneOf(generators.toList()).generate().generate() }
    fun <T> oneOf(values: List<T>) = create { values[RANDOM.nextInt(values.size)] }
    fun string() = create { nextPrintableString(RANDOM.nextInt(100)) }
    fun int() = create { RANDOM.nextInt() }
    fun long() = create { RANDOM.nextLong() }
    fun bool() = create { RANDOM.nextBoolean() }
    fun double() = create { RANDOM.nextDouble() }
    fun float() = create { RANDOM.nextFloat() }
    fun <T> set(gen: Gen<T>) = create { (0..RANDOM.nextInt(100)).map { gen.generate() }.toSet() }
    fun <T> list(gen: Gen<T>) = create { (0..RANDOM.nextInt(100)).map { gen.generate() } }
    fun <T> nullable(gen: Gen<T>) = create { oneOf(create { null }, gen).generate() }

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
      return (0..length - 1).map { RANDOM.nextPrintableChar() }.joinToString("")
    }
  }
}

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
  val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
  override fun compareTo(other: TypeReference<T>) = 0
}

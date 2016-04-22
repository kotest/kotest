package io.kotlintest.properties

import com.sksamuel.koors.Random

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

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> default(): Gen<T> {
      return when (T::class.simpleName) {
        String::class.simpleName -> Gen.string() as Gen<T>
        Int::class.simpleName -> Gen.int() as Gen<T>
        Long::class.simpleName -> Gen.long() as Gen<T>
        Boolean::class.simpleName -> Gen.bool() as Gen<T>
        Float::class.simpleName -> Gen.float() as Gen<T>
        Double::class.simpleName -> Gen.double() as Gen<T>
        else -> throw IllegalArgumentException("Cannot infer generator for ${T::class.simpleName}; specify generators explicitly")
      }
    }
  }

  fun nextPrintableString(length: Int): String {
    return (0..length).map { Random.Companion.default.nextPrintableChar() }.joinToString("")
  }
}
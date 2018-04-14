package io.kotlintest.properties

import io.kotlintest.JavaRandoms
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.math.BigInteger
import java.util.*

/** A shared random number generator. */
private val RANDOM = Random()

class BigIntegerGen(maxNumBits: Int) : Gen<BigInteger> {

  private val numBitsGen: Gen<Int> = Gen.choose(0, maxNumBits)

  override fun constants(): Iterable<BigInteger> = emptyList()
  override fun random(): Sequence<BigInteger> =
      numBitsGen.random().map { it.toBigInteger() }
}

/**
 * A Generator, or [Gen] is responsible for generating data
 * to be used in property testing. Each generator will
 * generate data for a specific type <T>.
 *
 * The idea behind property testing is the testing framework
 * will automatically test a range of different values,
 * including edge cases and random values.
 *
 * There are two types of values to consider.
 *
 * The first are values that should always be included - those
 * edge cases values which are common sources of bugs. For
 * example, a generator for [Int]s should always include
 * values like zero, minus 1, positive 1, Integer.MAX_VALUE
 * and Integer.MIN_VALUE.
 *
 * Another example would be for a generator for enums. That
 * should include _all_ the values of the enum to ensure
 * each value is tested.
 *
 * The second set of values are random values, which are
 * used to give us a greater breadth of values tested.
 * The [Int] generator example should return a random int
 * from across the entire integer range.
 */
interface Gen<T> {

  /**
   * Returns the values that should always be used
   * if this generator is to give complete coverage.
   */
  fun constants(): Iterable<T>

  /**
   * Generate a random sequence of type T, that is compatible
   * with the constraints of this generator.
   */
  fun random(): Sequence<T>

  /**
   * Given a value, T, this function should attempt to return
   * a 'smaller' value, for some definition of smaller.
   *
   * If the value cannot be shrunk further, or the type
   * doesn't support shrinking, then this method should
   * return the input value.
   *
   * For example, given a String, this function
   */
  fun shrink(t: T): T = t

  /**
   * Create a new [Gen] by filtering the output of this gen.
   */
  fun filter(f: (T) -> Boolean): Gen<T> {
    val outer = this
    return object : Gen<T> {
      override fun constants(): Iterable<T> = outer.constants().filter(f)
      override fun random(): Sequence<T> = outer.random().filter(f)
      override fun shrink(t: T): T {
        val t2 = outer.shrink(t)
        return if (f(t2)) t2 else t
      }
    }
  }

  fun filterNot(f: (T) -> Boolean): Gen<T> = filter { !f(it) }

  /**
   * Create a new [Gen] by mapping the output of this gen.
   */
  fun <U> flatMap(f: (T) -> Gen<U>): Gen<U> {
    val outer = this
    return object : Gen<U> {
      override fun constants(): Iterable<U> = outer.constants().flatMap { f(it).constants() }
      override fun random(): Sequence<U> = outer.random().flatMap { f(it).random() }
    }
  }

  /**
   * Create a new [Gen] by mapping the output of this gen.
   */
  fun <U> map(f: (T) -> U): Gen<U> {
    val outer = this
    return object : Gen<U> {
      override fun constants(): Iterable<U> = outer.constants().map(f)
      override fun random(): Sequence<U> = outer.random().map(f)
    }
  }

  /**
   * Create a new [Gen] which will return the values of this gen plus null.
   */
  fun orNull(): Gen<T?> {
    val outer = this
    return object : Gen<T?> {
      override fun constants(): Iterable<T?> = outer.constants() + listOf(null)
      override fun random(): Sequence<T?> = outer.random().map { if (RANDOM.nextBoolean()) null else it }
      override fun shrink(t: T?): T? = if (t == null) null else outer.shrink(t)
    }
  }

  companion object {

    fun <A, T> bind(gena: Gen<A>, createFn: (A) -> T): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> = gena.random().map { createFn(it) }
    }

    fun <A, B, T> bind(gena: Gen<A>, genb: Gen<B>, createFn: (A, B) -> T): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> =
          gena.random().zip(genb.random()).map { createFn(it.first, it.second) }
    }

    fun <A, B, C, T> bind(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, createFn: (A, B, C) -> T): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> =
          gena.random().zip(genb.random()).zip(genc.random()).map { createFn(it.first.first, it.first.second, it.second) }
    }

    fun <A, B, C, D, T> bind(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>,
                             createFn: (A, B, C, D) -> T): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> =
          gena.random()
              .zip(genb.random())
              .zip(genc.random())
              .zip(gend.random())
              .map { createFn(it.first.first.first, it.first.first.second, it.first.second, it.second) }
    }

    fun <A, B, C, D, E, T> bind(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>,
                                createFn: (A, B, C, D, E) -> T): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> =
          gena.random()
              .zip(genb.random())
              .zip(genc.random())
              .zip(gend.random())
              .zip(gene.random())
              .map { createFn(it.first.first.first.first, it.first.first.first.second, it.first.first.second, it.first.second, it.second) }
    }

    fun <A, B, C, D, E, F, T> bind(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>,
                                   createFn: (A, B, C, D, E, F) -> T): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> =
          gena.random()
              .zip(genb.random())
              .zip(genc.random())
              .zip(gend.random())
              .zip(gene.random())
              .zip(genf.random())
              .map {
                createFn(
                    it.first.first.first.first.first,
                    it.first.first.first.first.second,
                    it.first.first.first.second,
                    it.first.first.second,
                    it.first.second,
                    it.second)
              }
    }

    fun <A, B, C, D, E, F, G, T> bind(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, geng: Gen<G>,
                                      createFn: (A, B, C, D, E, F, G) -> T): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> =
          gena.random()
              .zip(genb.random())
              .zip(genc.random())
              .zip(gend.random())
              .zip(gene.random())
              .zip(genf.random())
              .zip(geng.random())
              .map {
                createFn(
                    it.first.first.first.first.first.first,
                    it.first.first.first.first.first.second,
                    it.first.first.first.first.second,
                    it.first.first.first.second,
                    it.first.first.second,
                    it.first.second,
                    it.second)
              }
    }

    fun bigInteger(maxNumBits: Int = 32): Gen<BigInteger> = BigIntegerGen(maxNumBits)

    /**
     * Returns a stream of values, where each value is
     * random Int between the given min and max.
     */
    fun choose(min: Int, max: Int): Gen<Int> {
      assert(min < max, { "min must be < max" })
      return object : Gen<Int> {
        override fun constants(): Iterable<Int> = emptyList()
        override fun random(): Sequence<Int> =
            generateSequence { JavaRandoms.internalNextInt(RANDOM, min, max) }

        override fun shrink(t: Int): Int = when (t) {
          min -> min
          else -> {
            val gap = (t - min)
            if (gap < 10) t - 1 else t - ((t - min) / 2)
          }
        }
      }
    }

    /**
     * Returns a stream of values, where each value is a
     * Long between the given min and max.
     */
    fun choose(min: Long, max: Long): Gen<Long> {
      assert(min < max, { "min must be < max" })
      return object : Gen<Long> {
        override fun constants(): Iterable<Long> = emptyList()
        override fun random(): Sequence<Long> = generateSequence { JavaRandoms.internalNextLong(RANDOM, min, max) }
      }
    }


    /**
     * Adapts a list into a generator, where random
     * values will be picked. May not choose every
     * item in the list.
     */
    fun <T> from(values: List<T>): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> = generateInfiniteSequence { values[JavaRandoms.internalNextInt(RANDOM, 0, values.size)] }
    }

    fun <T> from(values: Array<T>): Gen<T> = from(values.toList())

    inline fun <reified T : Enum<T>> enum(): Gen<T> = object : Gen<T> {
      val values = T::class.java.enumConstants.toList()
      override fun constants(): Iterable<T> = values
      override fun random(): Sequence<T> = from(values).random()
    }

    /**
     * Returns a stream of values where each value is a random
     * printed string.
     *
     * The constant values are:
     * The empty string
     * A line separator
     * Multi-line string
     * a UTF8 string.
     */
    fun string(): Gen<String> = object : Gen<String> {
      val literals = listOf("\n", "\nabc\n123\n", "\u006c\u0069b/\u0062\u002f\u006d\u0069nd/m\u0061x\u002e\u0070h\u0070")
      override fun constants(): Iterable<String> = literals
      override fun random(): Sequence<String> = generateSequence { nextPrintableString(RANDOM.nextInt(100)) }
      override fun shrink(t: String): String = when {
        t.isEmpty() || t.length == 1 -> ""
        else -> {
          val length = t.length / 2
          t.take(length)
        }
      }
    }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen [Int]. The values always returned include
     * the following edge cases: [-1, 0, 1, Int.MIN_VALUE, Int.MAX_VALUE]
     */
    fun int() = object : Gen<Int> {
      val literals = listOf(Int.MIN_VALUE, Int.MAX_VALUE, 0)
      override fun constants(): Iterable<Int> = literals
      override fun random(): Sequence<Int> = generateSequence { RANDOM.nextInt() }
      override fun shrink(t: Int): Int = when (t) {
        0 -> 0
        1, -1 -> 0
        in 2..10 -> t - 1
        in -10..-2 -> t + 1
        else -> t / 2
      }
    }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen positive value. The values returned always include
     * the following edge cases: [Int.MAX_VALUE]
     */
    fun positiveIntegers(): Gen<Int> = int().filter { it > 0 }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen natural number. The values returned always include
     * the following edge cases: [Int.MAX_VALUE]
     */
    fun nats(): Gen<Int> = int().filter { it >= 0 }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen negative value. The values returned always include
     * the following edge cases: [Int.MIN_VALUE]
     */
    fun negativeIntegers(): Gen<Int> = int().filter { it < 0 }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen created File object. The file objects do not necessarily
     * exist on disk.
     */
    fun file(): Gen<File> = object : Gen<File> {
      override fun constants(): Iterable<File> = emptyList()
      override fun random(): Sequence<File> = generateSequence { File(nextPrintableString(RANDOM.nextInt(100))) }
    }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen long. The values returned always include
     * the following edge cases: [Long.MIN_VALUE, Long.MAX_VALUE]
     */
    fun long(): Gen<Long> = object : Gen<Long> {
      val literals = listOf(Long.MIN_VALUE, Long.MAX_VALUE)
      override fun constants(): Iterable<Long> = literals
      override fun random(): Sequence<Long> = generateSequence { Math.abs(RANDOM.nextLong()) }
    }

    /**
     * Returns both boolean values
     */
    fun bool(): Gen<Boolean> = object : Gen<Boolean> {
      override fun constants(): Iterable<Boolean> = listOf(true, false)
      override fun random(): Sequence<Boolean> = generateSequence { RANDOM.nextBoolean() }
    }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen Double.
     */
    fun double(): Gen<Double> = object : Gen<Double> {
      val literals = listOf(Double.MIN_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.NaN, Double.POSITIVE_INFINITY)
      override fun constants(): Iterable<Double> = literals
      override fun random(): Sequence<Double> = generateSequence { RANDOM.nextDouble() }
      override fun shrink(t: Double): Double = when (t) {
        0.0 -> 0.0
        1.0, -1.0 -> 0.0
        in 2..10 -> t - 1
        in -10..-2 -> t + 1
        else -> t / 2
      }
    }

    fun positiveDoubles(): Gen<Double> = double().filter { it > 0.0 }
    fun negativeDoubles(): Gen<Double> = double().filter { it < 0.0 }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen Float.
     */
    fun float(): Gen<Float> = object : Gen<Float> {
      val literals = listOf(Float.MIN_VALUE, Float.MAX_VALUE, Float.NEGATIVE_INFINITY, Float.NaN, Float.POSITIVE_INFINITY)
      override fun constants(): Iterable<Float> = literals
      override fun random(): Sequence<Float> = generateSequence { RANDOM.nextFloat() }
    }

    /**
     * Returns a stream of values, where each
     * value is generated from the given function
     */
    inline fun <T> create(crossinline fn: () -> T): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> = generateInfiniteSequence { fn() }
    }

    /**
     * Returns a stream of values, where each value is
     * a set of values generated by the given generator.
     */
    fun <T> set(gen: Gen<T>): Gen<Set<T>> = object : Gen<Set<T>> {
      override fun constants(): Iterable<Set<T>> = listOf(gen.constants().toSet())
      override fun random(): Sequence<Set<T>> = generateSequence {
        val size = RANDOM.nextInt(100)
        gen.random().take(size).toSet()
      }
    }

    /**
     * Returns a stream of values, where each value is
     * a list of values generated by the underlying generator.
     */
    fun <T> list(gen: Gen<T>): Gen<List<T>> = object : Gen<List<T>> {
      override fun constants(): Iterable<List<T>> = listOf(gen.constants().toList())
      override fun random(): Sequence<List<T>> = generateSequence {
        val size = RANDOM.nextInt(100)
        gen.random().take(size).toList()
      }
    }

    /**
     * Returns a stream of values, where each value is
     * a pair generated by the underlying generators.
     */
    fun <K, V> pair(genK: Gen<K>, genV: Gen<V>): Gen<Pair<K, V>> = object : Gen<Pair<K, V>> {
      override fun constants(): Iterable<Pair<K, V>> {
        val keys = genK.constants().toList()
        return keys.zip(genV.random().take(keys.size).toList())
      }

      override fun random(): Sequence<Pair<K, V>> = genK.random().zip(genV.random())
    }

    // list(pair(genK, genV)).generate().toMap()

    /**
     * Returns a stream of values, where each value is
     * a Map, which contains keys and values generated
     * from the underlying generators.
     */
    fun <K, V> map(genK: Gen<K>, genV: Gen<V>): Gen<Map<K, V>> = object : Gen<Map<K, V>> {
      override fun constants(): Iterable<Map<K, V>> = emptyList()
      override fun random(): Sequence<Map<K, V>> = generateSequence {
        val size = RANDOM.nextInt(100)
        genK.random().take(size).zip(genV.random().take(size)).toMap()
      }
    }

    fun <T> constant(value: T): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = listOf(value)
      override fun random(): Sequence<T> = generateInfiniteSequence { value }
    }

    fun forClassName(className: String): Gen<*> {
      return when (className) {
        "java.lang.String" -> string()
        "kotlin.String" -> string()
        "java.lang.Integer" -> int()
        "kotlin.Int" -> int()
        "java.lang.Long" -> long()
        "kotlin.Long" -> long()
        "java.lang.Boolean" -> bool()
        "kotlin.Boolean" -> bool()
        "java.lang.Float" -> float()
        "kotlin.Float" -> float()
        "java.lang.Double" -> double()
        "kotlin.Double" -> double()
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
          list(forClassName(upper.name) as Gen<Any>) as Gen<T>
        }
        Set::class.qualifiedName -> {
          val type = object : TypeReference<T>() {}.type as ParameterizedType
          val first = type.actualTypeArguments.first() as WildcardType
          val upper = first.upperBounds.first() as Class<*>
          set(forClassName(upper.name) as Gen<Any>) as Gen<T>
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
    val low = 32
    val high = 127
    return (nextInt(high - low) + low).toChar()
  }

  fun nextPrintableString(length: Int): String {
    return (0 until length).map { RANDOM.nextPrintableChar() }.joinToString("")
  }
}

/**
 * A Generator which will return an iterable of a single given value.
 */
@Deprecated("use Gen.constant")
data class ConstGen<T : Any>(val value: T) : Gen<T> {
  override fun constants(): Iterable<T> = listOf(value)
  override fun random(): Sequence<T> = generateSequence { value }
}

/**
 * An extension function for [Gen] that filters values
 * from an underlying generator using a predicate function.
 */
@Deprecated("use gen.filter(T -> Boolean", ReplaceWith("generate().filter(isGood)"))
internal fun <T> Gen<T>.generateGood(isGood: (T) -> Boolean) = filter(isGood)

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
  val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
  override fun compareTo(other: TypeReference<T>) = 0
}

/**
 * Create a new [Gen] by keeping only instances of U generated by this gen.
 * This is useful if you have a type hierarchy and only want to retain
 * a particular subtype.
 */
inline fun <T, reified U : T> Gen<T>.filterIsInstance(): Gen<U> {
  val outer = this
  return object : Gen<U> {
    override fun constants(): Iterable<U> = outer.constants().filterIsInstance<U>()
    override fun random(): Sequence<U> = outer.random().filterIsInstance<U>()
  }
}

inline fun <T> generateInfiniteSequence(crossinline generator: () -> T): Sequence<T> =
        Sequence({ object: Iterator<T> {
          override fun hasNext() = true

          override fun next() = generator()

        } })
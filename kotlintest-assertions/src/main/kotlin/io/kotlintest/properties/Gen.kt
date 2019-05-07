package io.kotlintest.properties

import arrow.higherkind
import io.kotlintest.properties.shrinking.*
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.math.BigInteger
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.random.Random

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
@higherkind
interface Gen<T> : GenOf<T> {

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

  fun shrinker(): Shrinker<T>? = null

  /**
   * Create a new [Gen] by filtering the output of this gen.
   */
  fun filter(pred: (T) -> Boolean): Gen<T> {
    val outer = this
    return object : Gen<T> {
      override fun constants(): Iterable<T> = outer.constants().filter(pred)
      override fun random(): Sequence<T> = outer.random().filter(pred)
      override fun shrinker(): Shrinker<T>? {
        val s = outer.shrinker()
        return if (s == null) null else object : Shrinker<T> {
          override fun shrink(failure: T): List<T> = s.shrink(failure).filter(pred)
        }
      }
    }
  }

  fun filterNot(f: (T) -> Boolean): Gen<T> = filter { !f(it) }

  /**
   * Create a new [Gen] by mapping the output of this gen.
   */
  fun <U> flatMap(f: (T) -> GenOf<U>): Gen<U> {
    val outer = this
    return object : Gen<U> {
      override fun constants(): Iterable<U> = outer.constants().flatMap { f(it).fix().constants() }
      override fun random(): Sequence<U> = outer.random().flatMap { f(it).fix().random() }
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
      override fun random(): Sequence<T?> = outer.random().map { if (Random.nextBoolean()) null else it }
      override fun shrinker(): Shrinker<T?>? {
        val s = outer.shrinker()
        return if (s == null) null else object : Shrinker<T?> {
          override fun shrink(failure: T?): List<T?> = if (failure == null) emptyList() else s.shrink(failure)
        }
      }
    }
  }

  /**
   * Returns a new [[Gen]] which will return the values from this gen and the values of
   * the supplied gen together. The supplied gen must be a subtype of the
   * type of this gen.
   */
  fun <U : T> merge(gen: Gen<U>): Gen<T> {
    val outer = this
    return object : Gen<T> {
      override fun constants(): Iterable<T> = outer.constants() + gen.constants()
      override fun random(): Sequence<T> = outer.random().zip(gen.random()).flatMap { sequenceOf(it.first, it.second) }
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

    fun <A> oneOf(vararg gens: Gen<out A>): Gen<A> = object : Gen<A> {
      override fun constants(): Iterable<A> = gens.flatMap { it.constants() }

      override fun random(): Sequence<A> {
        assert(gens.isNotEmpty()) { "List of generators cannot be empty" }

        val iterators = gens.map { it.random().iterator() }

        return generateInfiniteSequence {
          val iteratorLocation = Random.nextInt(0, iterators.size)
          val iterator = iterators[iteratorLocation]
          iterator.next()
        }

      }

    }

    fun bigInteger(maxNumBits: Int = 32): Gen<BigInteger> = BigIntegerGen(maxNumBits)

    /**
     * Returns a stream of values, where each value is
     * a random Int between the given min and max.
     */
    fun choose(min: Int, max: Int): Gen<Int> {
      assert(min < max) { "min must be < max" }
      return object : Gen<Int> {
        override fun constants(): Iterable<Int> = emptyList()
        override fun random(): Sequence<Int> =
            generateSequence { Random.nextInt(min, max) }

        override fun shrinker() = ChooseShrinker(min, max)
      }
    }

    /**
     * Returns a stream of values, where each value is a
     * Long between the given min and max.
     */
    fun choose(min: Long, max: Long): Gen<Long> {
      assert(min < max) { "min must be < max" }
      return object : Gen<Long> {
        override fun constants(): Iterable<Long> = emptyList()
        override fun random(): Sequence<Long> = generateSequence { Random.nextLong(min, max) }
      }
    }


    /**
     * Adapts a list into a generator, where random
     * values will be picked. May not choose every
     * item in the list.
     */
    fun <T> from(values: List<T>): Gen<T> = object : Gen<T> {
      override fun constants(): Iterable<T> = emptyList()
      override fun random(): Sequence<T> = generateInfiniteSequence { values[Random.nextInt(0, values.size)] }
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
    @JvmOverloads
    fun string(maxSize: Int = 100): Gen<String> = object : Gen<String> {
      val literals = listOf("", "\n", "\nabc\n123\n", "\u006c\u0069b/\u0062\u002f\u006d\u0069nd/m\u0061x\u002e\u0070h\u0070")
      override fun constants(): Iterable<String> = literals
      override fun random(): Sequence<String> = generateSequence { nextPrintableString(Random.nextInt(maxSize)) }
      override fun shrinker(): Shrinker<String>? = StringShrinker
    }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen [Int]. The values always returned include
     * the following edge cases: [Int.MIN_VALUE, Int.MAX_VALUE, 0]
     */
    fun int() = object : Gen<Int> {
      val literals = listOf(Int.MIN_VALUE, Int.MAX_VALUE, 0)
      override fun constants(): Iterable<Int> = literals
      override fun random(): Sequence<Int> = generateSequence { Random.nextInt() }
      override fun shrinker() = IntShrinker
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
      override fun random(): Sequence<File> = generateSequence { File(nextPrintableString(Random.nextInt(100))) }
    }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen long. The values returned always include
     * the following edge cases: [Long.MIN_VALUE, Long.MAX_VALUE]
     */
    fun long(): Gen<Long> = object : Gen<Long> {
      val literals = listOf(Long.MIN_VALUE, Long.MAX_VALUE)
      override fun constants(): Iterable<Long> = literals
      override fun random(): Sequence<Long> = generateSequence { Math.abs(Random.nextLong()) }
    }

    /**
     * Returns both boolean values
     */
    fun bool(): Gen<Boolean> = object : Gen<Boolean> {
      override fun constants(): Iterable<Boolean> = listOf(true, false)
      override fun random(): Sequence<Boolean> = generateSequence { Random.nextBoolean() }
    }

    fun uuid(): Gen<UUID> = object : Gen<UUID> {
      override fun constants(): Iterable<UUID> = emptyList()
      override fun random(): Sequence<UUID> = generateSequence { UUID.randomUUID() }
    }

    /**
     * Generates a stream of random LocalDates
     *
     * This generator creates randomly generated LocalDates, in the range [[minYear, maxYear]].
     *
     * If any of the years in the range contain a leap year, the date [29/02/YEAR] will always be a constant value of this
     * generator.
     *
     * @see [localDateTime]
     * @see [localTime]
     */
    fun localDate(minYear: Int = 1970, maxYear: Int = 2030): Gen<LocalDate> = object : Gen<LocalDate> {
      override fun constants(): Iterable<LocalDate> {
        val yearRange = (minYear..maxYear)
        val feb28Date = LocalDate.of(yearRange.random(), 2, 28)

        val feb29Year = yearRange.firstOrNull { Year.of(it).isLeap }
        val feb29Date = feb29Year?.let { LocalDate.of(it, 2, 29) }

        return listOfNotNull(feb28Date, feb29Date, LocalDate.of(minYear, 1, 1), LocalDate.of(maxYear, 12, 31))
      }
      override fun random(): Sequence<LocalDate> = generateSequence {
        val minDate = LocalDate.of(minYear, 1, 1)
        val maxDate = LocalDate.of(maxYear, 12, 31)

        val days = ChronoUnit.DAYS.between(minDate, maxDate)

        minDate.plusDays(Random.nextLong(days + 1))
      }
    }

    /**
     * Generates a stream of random LocalTimes
     *
     * This generator creates randomly generated LocalTimes.
     *
     * @see [localDateTime]
     * @see [localDate]
     */
    fun localTime(): Gen<LocalTime> = object : Gen<LocalTime> {
      override fun constants(): Iterable<LocalTime> = listOf(LocalTime.of(23, 59, 59), LocalTime.of(0, 0, 0))
      override fun random(): Sequence<LocalTime> = generateSequence {
        LocalTime.of(Random.nextInt(24), Random.nextInt(60), Random.nextInt(60))
      }
    }

    /**
     * Generates a stream of random LocalDateTimes
     *
     * This generator creates randomly generated LocalDates, in the range [[minYear, maxYear]].
     *
     * If any of the years in the range contain a leap year, the date [29/02/YEAR] will always be a constant value of this
     * generator.
     *
     * @see [localDateTime]
     * @see [localTime]
     */
    fun localDateTime(minYear: Int = 1970, maxYear: Int = 2030): Gen<LocalDateTime> = object : Gen<LocalDateTime> {
      override fun constants(): Iterable<LocalDateTime> {
        val localDates = localDate(minYear, maxYear).constants()
        val times = localTime().constants()

        return localDates.flatMap { date -> times.map { date.atTime(it) } }
      }

      override fun random(): Sequence<LocalDateTime> {
        val dateSequence = localDate(minYear, maxYear).random().iterator()
        val timeSequence = localTime().random().iterator()

        return generateSequence { dateSequence.next().atTime(timeSequence.next()) }
      }
    }

    /**
     * Generates a stream of random Durations
     *
     * This generator creates randomly generated Duration, of at most [maxDuration].
     */
    fun duration(maxDuration: Duration = Duration.ofDays(10)): Gen<Duration> = object : Gen<Duration> {
      private val maxDurationInSeconds = maxDuration.seconds

      override fun constants(): Iterable<Duration> = listOf(Duration.ZERO)
      override fun random(): Sequence<Duration> = generateSequence { Duration.ofSeconds(Random.nextLong(maxDurationInSeconds)) }
    }

    /**
     * Generates a stream of random Periods
     *
     * This generator creates randomly generated Periods, with years less than or equal to [maxYear].
     *
     * If [maxYear] is 0, only random months and days will be generated.
     *
     * Months will always be in range [0..11]
     * Days will always be in range [0..31]
     */
    fun period(maxYear: Int = 10): Gen<Period> = object : Gen<Period> {

      override fun constants(): Iterable<Period> = listOf(Period.ZERO)
      override fun random(): Sequence<Period> = generateSequence {
        Period.of((0..maxYear).random(), (0..11).random(), (0..31).random())
      }
    }

    /**
     * Returns a stream of values where each value is a randomly
     * chosen Double.
     */
    fun double(): Gen<Double> = object : Gen<Double> {
      val literals = listOf(0.0, 1.0, -1.0, 1e300, Double.MIN_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.NaN, Double.POSITIVE_INFINITY)
      override fun constants(): Iterable<Double> = literals
      override fun random(): Sequence<Double> = generateSequence { Random.nextDouble() }
      override fun shrinker(): Shrinker<Double>? = DoubleShrinker
    }

    /**
     * Returns a [Gen] which is the same as [Gen.double] but does not include +INFINITY, -INFINITY or NaN.
     *
     * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
     */
    fun numericDoubles(from: Double = Double.MIN_VALUE,
                       to: Double = Double.MAX_VALUE
    ): Gen<Double> = object : Gen<Double> {
      val literals = listOf(0.0, 1.0, -1.0, 1e300, Double.MIN_VALUE, Double.MAX_VALUE).filter { it in (from..to) }
      override fun constants(): Iterable<Double> = literals
      override fun random(): Sequence<Double> = generateSequence { Random.nextDouble(from, to) }
      override fun shrinker(): Shrinker<Double>? = DoubleShrinker
    }

    fun positiveDoubles(): Gen<Double> = double().filter { it > 0.0 }
    fun negativeDoubles(): Gen<Double> = double().filter { it < 0.0 }


    /**
     * Returns a stream of values where each value is a randomly
     * chosen Float.
     */
    fun float(): Gen<Float> = object : Gen<Float> {
      val literals = listOf(0F, Float.MIN_VALUE, Float.MAX_VALUE, Float.NEGATIVE_INFINITY, Float.NaN, Float.POSITIVE_INFINITY)
      override fun constants(): Iterable<Float> = literals
      override fun random(): Sequence<Float> = generateSequence { Random.nextFloat() }
      override fun shrinker() = FloatShrinker
    }

    /**
     * Returns a [Gen] which is the same as [Gen.float] but does not include +INFINITY, -INFINITY or NaN.
     *
     * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
     */
    fun numericFloats(
            from: Float = Float.MIN_VALUE,
            to: Float = Float.MAX_VALUE
    ): Gen<Float> = object : Gen<Float> {
      val literals = listOf(0.0F, 1.0F, -1.0F, Float.MIN_VALUE, Float.MAX_VALUE).filter { it in (from..to) }
      override fun constants(): Iterable<Float> = literals

      // There's no nextFloat(from, to) method, so borrowing it from Double
      override fun random(): Sequence<Float> = generateSequence { Random.nextDouble(from.toDouble(), to.toDouble()).toFloat() }
      override fun shrinker(): Shrinker<Float>? = FloatShrinker
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
    @JvmOverloads
    fun <T> set(gen: Gen<T>, maxSize: Int = 100): Gen<Set<T>> = object : Gen<Set<T>> {
      init {require(maxSize >= 0) {"maxSize must be positive"}}
      override fun constants(): Iterable<Set<T>> = listOf(gen.constants().take(maxSize).toSet())
      override fun random(): Sequence<Set<T>> = generateSequence {
        val size = Random.nextInt(maxSize)
        gen.random().take(size).toSet()
      }
    }

    /**
     * Returns a stream of values, where each value is
     * a list of values generated by the underlying generator.
     */
    @JvmOverloads
    fun <T> list(gen: Gen<T>, maxSize: Int = 100): Gen<List<T>> = object : Gen<List<T>> {
      init {require(maxSize >= 0) {"maxSize must be positive"}}
      override fun constants(): Iterable<List<T>> = listOf(gen.constants().take(maxSize).toList())
      override fun random(): Sequence<List<T>> = generateSequence {
        val size = Random.nextInt(maxSize)
        gen.random().take(size).toList()
      }

      override fun shrinker() = ListShrinker<T>()
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

    /**
     * Returns a [[Gen]] where each value is a [[Triple]] generated
     * by a value from each of three supplied generators.
     */
    fun <A, B, C> triple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>): Gen<Triple<A, B, C>> = object : Gen<Triple<A, B, C>> {
      override fun constants(): Iterable<Triple<A, B, C>> {
        return genA.constants().zip(genB.constants()).zip(genC.constants()).map { Triple(it.first.first, it.first.second, it.second) }
      }

      override fun random(): Sequence<Triple<A, B, C>> = genA.random().zip(genB.random()).zip(genC.random()).map { Triple(it.first.first, it.first.second, it.second) }
    }

    /**
     * Returns a stream of values, where each value is
     * a Map, which contains keys and values generated
     * from the underlying generators.
     */
    @JvmOverloads
    fun <K, V> map(genK: Gen<K>, genV: Gen<V>, maxSize: Int = 100): Gen<Map<K, V>> = object : Gen<Map<K, V>> {
      init {require(maxSize >= 0) {"maxSize must be positive"}}
      override fun constants(): Iterable<Map<K, V>> = emptyList()
      override fun random(): Sequence<Map<K, V>> = generateSequence {
        val size = Random.nextInt(maxSize)
        genK.random().take(size).zip(genV.random().take(size)).toMap()
      }
    }

    fun <K, V> map(gen: Gen<Pair<K,V>>, maxSize: Int = 100): Gen<Map<K, V>> = object : Gen<Map<K, V>> {
      init {require(maxSize >= 0) {"maxSize must be positive"}}
      override fun constants(): Iterable<Map<K, V>> = emptyList()
      override fun random(): Sequence<Map<K, V>> = generateSequence {
        val size = Random.nextInt(maxSize)
        gen.random().take(size).toMap()
      }
    }

    /**
     * Returns a [[Gen]] which always returns the same value.
     */
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
        "java.util.UUID" -> uuid()
        "java.io.File" -> file()
        "java.time.LocalDate" -> localDate()
        "java.time.LocalDateTime" -> localDateTime()
        "java.time.LocalTime" -> localTime()
        "java.time.Duration" -> duration()
        "java.time.Period" -> period()
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
    return (0 until length).map { Random.nextPrintableChar() }.joinToString("")
  }

  /**
   * Draws [amount] values from this generator
   *
   * This method will draw values from the generator until it generates [amount] values. This first draws from the
   * [constants] pool, and if necessary starts drawing from the [random] pool.
   *
   * This is useful if you want the generated values, but don't want to execute a property test over them (for example,
   * by using [assertAll] or [forAll]
   *
   * ```
   * val gen = Gen.string()
   * val generatedValues: List<String> = gen.take(20)
   * ```
   */
  fun take(amount: Int): List<T> {
    require(amount > 0) { "Amount must be > 0, but was $amount" }

    val generatedValues = (constants() + random().take(amount)).take(amount)
    val generatedSize = generatedValues.size

    check(generatedSize == amount) { "Gen could only generate $generatedSize values while you requested $amount." }
    return generatedValues
  }

  /**
   * Draws a random value from this generator
   *
   * This method will draw a single value from the [random] values, that matches [predicate] (defaults to every
   * value)
   *
   * This expects that [random] will return an infinite, random sequence. Due to this, a call to [Sequence.first] is
   * made. As usually random is infinite, this should always return a different value. For fixed sequences, this will
   * always return the first value of the sequence.
   *
   * This is useful if you want a randomized value, but don't want to execute a property test over them (for example, by
   * using [assertAll] or [forAll]).
   *
   * IMPORTANT: This will not draw from the [constants] pool. Only [random] values.
   *
   * ```
   * val gen = Gen.string()
   * val generatedValue: String = gen.next()
   * val filteredValue: String = gen.next { it != "hello" }
   * ```
   */
  fun next(predicate: (T) -> Boolean = { true }): T {
    return random().first(predicate)
  }
}

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
    Sequence {
      object : Iterator<T> {
        override fun hasNext() = true
        override fun next() = generator()
      }
    }

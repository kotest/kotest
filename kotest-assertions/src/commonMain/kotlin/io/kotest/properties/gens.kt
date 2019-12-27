package io.kotest.properties

import io.kotest.properties.shrinking.ChooseShrinker
import io.kotest.properties.shrinking.DoubleShrinker
import io.kotest.properties.shrinking.FloatShrinker
import io.kotest.properties.shrinking.IntShrinker
import io.kotest.properties.shrinking.ListShrinker
import io.kotest.properties.shrinking.Shrinker
import io.kotest.properties.shrinking.StringShrinker
import kotlin.jvm.JvmOverloads
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

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
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.string(minSize: Int = 0, maxSize: Int = 100): Gen<String> = object : Gen<String> {
   val literals = listOf("",
      "\n",
      "\nabc\n123\n",
      "\u006c\u0069b/\u0062\u002f\u006d\u0069nd/m\u0061x\u002e\u0070h\u0070")

   override fun constants(): Iterable<String> = literals.filter { it.length in minSize..maxSize }
   override fun random(seed: Long?): Sequence<String> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence {
         r.nextPrintableString(minSize + r.nextInt(maxSize - minSize + 1))
      }
   }
   override fun shrinker(): Shrinker<String>? = StringShrinker
}

/**
 * Returns a stream of values where each value is a randomly
 * chosen [Int]. The values always returned include
 * the following edge cases: [[Int.MIN_VALUE], [Int.MAX_VALUE], 0]
 */
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.int() = object : Gen<Int> {
   val literals = listOf(Int.MIN_VALUE, Int.MAX_VALUE, 0)
   override fun constants(): Iterable<Int> = literals
   override fun random(seed: Long?): Sequence<Int> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { r.nextInt() }
   }

   override fun shrinker() = IntShrinker
}

/**
 * Returns a stream of values where each value is a randomly
 * chosen [UInt]. The values always returned include
 * the following edge cases: [[UInt.MIN_VALUE], [UInt.MAX_VALUE]]
 */
@ExperimentalUnsignedTypes
fun Gen.Companion.uint() = object : Gen<UInt> {
   val literals = listOf(UInt.MIN_VALUE, UInt.MAX_VALUE)
   override fun constants(): Iterable<UInt> = literals
   override fun random(seed: Long?): Sequence<UInt> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { r.nextInt().toUInt() }
   }
}

/**
 * Returns a stream of values where each value is a randomly
 * chosen [Short]. The values always returned include
 * the following edge cases: [[Short.MIN_VALUE], [Short.MAX_VALUE], 0]
 */
fun Gen.Companion.short() = int().map { it.ushr(Int.SIZE_BITS - Short.SIZE_BITS).toShort() }

/**
 * Returns a stream of values where each value is a randomly
 * chosen [UShort]. The values always returned include
 * the following edge cases: [[UShort.MIN_VALUE], [UShort.MAX_VALUE]]
 */
@ExperimentalUnsignedTypes
fun Gen.Companion.ushort() = uint().map { it.shr(UInt.SIZE_BITS - UShort.SIZE_BITS).toUShort() }

/**
 * Returns a stream of values where each value is a randomly
 * chosen [Byte]. The values always returned include
 * the following edge cases: [[Byte.MIN_VALUE], [Byte.MAX_VALUE], 0]
 */
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.byte() = int().map { it.ushr(Int.SIZE_BITS - Byte.SIZE_BITS).toByte() }

/**
 * Returns a stream of values where each value is a randomly
 * chosen [UByte]. The values always returned include
 * the following edge cases: [[UByte.MIN_VALUE], [UByte.MAX_VALUE]]
 */
@ExperimentalUnsignedTypes
fun Gen.Companion.ubyte() = uint().map { it.shr(UInt.SIZE_BITS - UByte.SIZE_BITS).toByte() }

/**
 * Returns a stream of values where each value is a randomly
 * chosen positive value. The values returned always include
 * the following edge cases: [Int.MAX_VALUE]
 */
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.positiveIntegers(): Gen<Int> = int().filter { it > 0 }

/**
 * Returns a stream of values where each value is a randomly
 * chosen natural number. The values returned always include
 * the following edge cases: [Int.MAX_VALUE]
 */
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.nats(): Gen<Int> = int().filter { it >= 0 }

/**
 * Returns a stream of values where each value is a randomly
 * chosen negative value. The values returned always include
 * the following edge cases: [Int.MIN_VALUE]
 */
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.negativeIntegers(): Gen<Int> = int().filter { it < 0 }

/**
 * Returns a stream of values where each value is a randomly
 * chosen Double.
 */
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.double(): Gen<Double> = object : Gen<Double> {
  val literals = listOf(0.0,
    1.0,
    -1.0,
    1e300,
    Double.MIN_VALUE,
    Double.MAX_VALUE,
    Double.NEGATIVE_INFINITY,
    Double.NaN,
    Double.POSITIVE_INFINITY)

  override fun constants(): Iterable<Double> = literals
   override fun random(seed: Long?): Sequence<Double> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { r.nextDouble() }
   }
  override fun shrinker(): Shrinker<Double>? = DoubleShrinker
}

/**
 * Returns a [Gen] which is the same as [Gen.double] but does not include +INFINITY, -INFINITY or NaN.
 *
 * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
 */
fun Gen.Companion.numericDoubles(from: Double = Double.MIN_VALUE,
                                 to: Double = Double.MAX_VALUE
): Gen<Double> = object : Gen<Double> {
   val literals = listOf(0.0, 1.0, -1.0, 1e300, Double.MIN_VALUE, Double.MAX_VALUE).filter { it in (from..to) }
   override fun constants(): Iterable<Double> = literals
   override fun random(seed: Long?): Sequence<Double> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { r.nextDouble(from, to) }
   }

   override fun shrinker(): Shrinker<Double>? = DoubleShrinker
}

@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.positiveDoubles(): Gen<Double> = double().filter { it > 0.0 }

@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.negativeDoubles(): Gen<Double> = double().filter { it < 0.0 }


/**
 * Returns a stream of values where each value is a randomly
 * chosen Float.
 */
fun Gen.Companion.float(): Gen<Float> = object : Gen<Float> {
  val literals = listOf(0F,
    Float.MIN_VALUE,
    Float.MAX_VALUE,
    Float.NEGATIVE_INFINITY,
    Float.NaN,
    Float.POSITIVE_INFINITY)

  override fun constants(): Iterable<Float> = literals
   override fun random(seed: Long?): Sequence<Float> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { r.nextFloat() }
   }
  override fun shrinker() = FloatShrinker
}

/**
 * Returns a [Gen] which is the same as [Gen.float] but does not include +INFINITY, -INFINITY or NaN.
 *
 * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
 */
fun Gen.Companion.numericFloats(
   from: Float = Float.MIN_VALUE,
   to: Float = Float.MAX_VALUE
): Gen<Float> = object : Gen<Float> {
   val literals = listOf(0.0F, 1.0F, -1.0F, Float.MIN_VALUE, Float.MAX_VALUE).filter { it in (from..to) }
   override fun constants(): Iterable<Float> = literals

   // There's no nextFloat(from, to) method, so borrowing it from Double
   override fun random(seed: Long?): Sequence<Float> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence {
         r.nextDouble(from.toDouble(), to.toDouble()).toFloat()
      }
   }

   override fun shrinker(): Shrinker<Float>? = FloatShrinker
}

/**
 * Returns a stream of values where each value is a randomly
 * chosen long. The values returned always include
 * the following edge cases: [[Long.MIN_VALUE], [Long.MAX_VALUE]]
 */
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.long(): Gen<Long> = object : Gen<Long> {
  val literals = listOf(Long.MIN_VALUE, Long.MAX_VALUE)
  override fun constants(): Iterable<Long> = literals
   override fun random(seed: Long?): Sequence<Long> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { abs(r.nextLong()) }
   }
}

/**
 * Returns a stream of values where each value is a randomly
 * chosen [ULong]. The values returned always include
 * the following edge cases: [[ULong.MIN_VALUE], [ULong.MAX_VALUE]]
 */
@ExperimentalUnsignedTypes
fun Gen.Companion.ulong(): Gen<ULong> = object : Gen<ULong> {
  val literals = listOf(ULong.MIN_VALUE, ULong.MAX_VALUE)
  override fun constants(): Iterable<ULong> = literals
   override fun random(seed: Long?): Sequence<ULong> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { r.nextLong().toULong() }
   }
}

/**
 * Returns both boolean values
 */
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun Gen.Companion.bool(): Gen<Boolean> = object : Gen<Boolean> {
  override fun constants(): Iterable<Boolean> = listOf(true, false)
   override fun random(seed: Long?): Sequence<Boolean> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { r.nextBoolean() }
   }
}

private object CharSets {
  val CONTROL     = listOf('\u0000'..'\u001F', '\u007F'..'\u007F')
  val WHITESPACE  = listOf('\u0020'..'\u0020', '\u0009'..'\u0009', '\u000A'..'\u000A')
  val BASIC_LATIN = listOf('\u0021'..'\u007E')
}

/**
 * Returns a stream of randomly-chosen Chars. Custom characters can be generated by
 * providing CharRanges. Distribution will be even across the ranges of Chars.
 * For example:
 * Gen.char('A'..'C', 'D'..'E')
 * Ths will choose A, B, C, D, and E each 20% of the time.
 */
fun Gen.Companion.char(range: CharRange, vararg ranges: CharRange): Gen<Char> {
  return Gen.char(listOf(range) + ranges)
}

/**
 * Returns a stream of randomly-chosen Chars. Custom characters can be generated by
 * providing a list of CharRanges. Distribution will be even across the ranges of Chars.
 * For example:
 * Gen.char(listOf('A'..'C', 'D'..'E')
 * Ths will choose A, B, C, D, and E each 20% of the time.
 *
 * If no parameter is given, ASCII characters will be generated.
 */
fun Gen.Companion.char(ranges: List<CharRange> = CharSets.BASIC_LATIN): Gen<Char> = object : Gen<Char> {
  init {
    require(ranges.all { !it.isEmpty() }) { "Ranges cannot be empty" }
    require(ranges.isNotEmpty()) { "List of ranges must have at least one range" }
  }
  override fun constants(): Iterable<Char> = emptyList()
  override fun random(seed: Long?): Sequence<Char> {
    val r = if (seed == null) Random.Default else Random(seed)
    val genRange =
      if (ranges.size == 1) Gen.constant(ranges.first())
      else makeRangeWeightedGen()
    return generateSequence { genRange.next(seed = seed).random(r) }
  }
  // Convert the list of CharRanges into a weighted Gen in which
  // the ranges are chosen from the list using the length of the
  // range as the weight.
  private fun makeRangeWeightedGen(): Gen<CharRange> {
     val weightPairs = ranges.map { range ->
        val weight = range.last.toInt() - range.first.toInt() + 1
        Pair(weight, range)
     }
     return Gen.choose(weightPairs[0], weightPairs[1], *weightPairs.drop(2).toTypedArray())
  }
}

/**
 * Returns a stream of values, where each value is
 * a set of values generated by the given generator.
 */
@JvmOverloads
fun <T> Gen.Companion.set(gen: Gen<T>, maxSize: Int = 100): Gen<Set<T>> = object : Gen<Set<T>> {
   init {
      require(maxSize >= 0) { "maxSize must be positive" }
   }

   override fun constants(): Iterable<Set<T>> = listOf(gen.constants().take(maxSize).toSet())
   override fun random(seed: Long?): Sequence<Set<T>> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence {
         val size = r.nextInt(maxSize)
         gen.random().take(size).toSet()
      }
   }
}

/**
 * Returns a stream of values, where each value is
 * a list of values generated by the underlying generator.
 */
@JvmOverloads
fun <T> Gen.Companion.list(gen: Gen<T>, maxSize: Int = 100): Gen<List<T>> = object : Gen<List<T>> {
   init {
      require(maxSize >= 0) { "maxSize must be positive" }
   }

   override fun constants(): Iterable<List<T>> = listOf(gen.constants().take(maxSize).toList())
   override fun random(seed: Long?): Sequence<List<T>> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence {
         val size = r.nextInt(maxSize)
         gen.random().take(size).toList()
      }
   }

   override fun shrinker() = ListShrinker<T>()
}

/**
 * Returns a [[Gen]] where each value is a [[Triple]] generated
 * by a value from each of three supplied generators.
 */
fun <A, B, C> Gen.Companion.triple(genA: Gen<A>,
                                   genB: Gen<B>,
                                   genC: Gen<C>): Gen<Triple<A, B, C>> = object : Gen<Triple<A, B, C>> {
  override fun constants(): Iterable<Triple<A, B, C>> {
    return genA.constants().zip(genB.constants()).zip(genC.constants()).map {
      Triple(it.first.first,
        it.first.second,
        it.second)
    }
  }

   override fun random(seed: Long?): Sequence<Triple<A, B, C>> = genA.random(seed).zip(genB.random(seed)).zip(
      genC.random(
         seed)).map {
    Triple(it.first.first,
      it.first.second,
      it.second)
  }
}

fun <A, T> Gen.Companion.bind(gena: Gen<A>, createFn: (A) -> T): Gen<T> = object : Gen<T> {
  override fun constants(): Iterable<T> = emptyList()
   override fun random(seed: Long?): Sequence<T> = gena.random().map { createFn(it) }
}

fun <A, B, T> Gen.Companion.bind(gena: Gen<A>, genb: Gen<B>, createFn: (A, B) -> T): Gen<T> = object : Gen<T> {
  override fun constants(): Iterable<T> = emptyList()
   override fun random(seed: Long?): Sequence<T> =
      gena.random().zip(genb.random(seed)).map { createFn(it.first, it.second) }
}

fun <A, B, C, T> Gen.Companion.bind(gena: Gen<A>,
                                    genb: Gen<B>,
                                    genc: Gen<C>,
                                    createFn: (A, B, C) -> T): Gen<T> = object : Gen<T> {
  override fun constants(): Iterable<T> = emptyList()
   override fun random(seed: Long?): Sequence<T> =
      gena.random().zip(genb.random(seed)).zip(genc.random()).map {
         createFn(it.first.first,
            it.first.second,
            it.second)
      }
}

fun <A, B, C, D, T> Gen.Companion.bind(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>,
                                       createFn: (A, B, C, D) -> T): Gen<T> = object : Gen<T> {
  override fun constants(): Iterable<T> = emptyList()
   override fun random(seed: Long?): Sequence<T> =
    gena.random()
       .zip(genb.random(seed))
       .zip(genc.random(seed))
       .zip(gend.random(seed))
      .map { createFn(it.first.first.first, it.first.first.second, it.first.second, it.second) }
}

fun <A, B, C, D, E, T> Gen.Companion.bind(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>,
                                          createFn: (A, B, C, D, E) -> T): Gen<T> = object : Gen<T> {
  override fun constants(): Iterable<T> = emptyList()
   override fun random(seed: Long?): Sequence<T> =
    gena.random()
       .zip(genb.random(seed))
       .zip(genc.random(seed))
       .zip(gend.random(seed))
       .zip(gene.random(seed))
      .map {
        createFn(it.first.first.first.first,
          it.first.first.first.second,
          it.first.first.second,
          it.first.second,
          it.second)
      }
}

fun <A, B, C, D, E, F, T> Gen.Companion.bind(gena: Gen<A>,
                                             genb: Gen<B>,
                                             genc: Gen<C>,
                                             gend: Gen<D>,
                                             gene: Gen<E>,
                                             genf: Gen<F>,
                                             createFn: (A, B, C, D, E, F) -> T): Gen<T> = object : Gen<T> {
  override fun constants(): Iterable<T> = emptyList()
   override fun random(seed: Long?): Sequence<T> =
    gena.random()
       .zip(genb.random(seed))
       .zip(genc.random(seed))
       .zip(gend.random(seed))
       .zip(gene.random(seed))
       .zip(genf.random(seed))
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

fun <A, B, C, D, E, F, G, T> Gen.Companion.bind(gena: Gen<A>,
                                                genb: Gen<B>,
                                                genc: Gen<C>,
                                                gend: Gen<D>,
                                                gene: Gen<E>,
                                                genf: Gen<F>,
                                                geng: Gen<G>,
                                                createFn: (A, B, C, D, E, F, G) -> T): Gen<T> = object : Gen<T> {
  override fun constants(): Iterable<T> = emptyList()
   override fun random(seed: Long?): Sequence<T> =
    gena.random()
       .zip(genb.random(seed))
       .zip(genc.random(seed))
       .zip(gend.random(seed))
       .zip(gene.random(seed))
       .zip(genf.random(seed))
       .zip(geng.random(seed))
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

fun <A> Gen.Companion.oneOf(vararg gens: Gen<out A>): Gen<A> = object : Gen<A> {
   override fun constants(): Iterable<A> = gens.flatMap { it.constants() }

   override fun random(seed: Long?): Sequence<A> {
      require(gens.isNotEmpty()) { "List of generators cannot be empty" }

      val iterators = gens.map { it.random(seed).iterator() }
      val r = if (seed == null) Random.Default else Random(seed)

      return generateInfiniteSequence {
         val iteratorLocation = r.nextInt(0, iterators.size)
         val iterator = iterators[iteratorLocation]
         iterator.next()
      }

   }
}

/**
 * Returns a stream of values, where each
 * value is generated from the given function
 */
inline fun <T> Gen.Companion.create(crossinline fn: () -> T): Gen<T> = object : Gen<T> {
  override fun constants(): Iterable<T> = emptyList()
   override fun random(seed: Long?): Sequence<T> = generateInfiniteSequence { fn() }
}

/**
 * Adapts a list into a generator, where random
 * values will be picked. May not choose every
 * item in the list.
 */
fun <T> Gen.Companion.from(values: List<T>): Gen<T> = object : Gen<T> {
  override fun constants(): Iterable<T> = emptyList()
   override fun random(seed: Long?): Sequence<T> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateInfiniteSequence { values[r.nextInt(0, values.size)] }
   }
}

/**
 * @return a new [Gen] created from the given [values] (see [from] List for more details)
 */
fun <T> Gen.Companion.from(values: Array<T>): Gen<T> = from(values.toList())

/**
 * Returns a stream of values, where each value is
 * a random Int between the given min and max.
 */
fun Gen.Companion.choose(min: Int, max: Int): Gen<Int> {
  require(min < max) { "min must be < max" }
  return object : Gen<Int> {
    override fun constants(): Iterable<Int> = emptyList()
     override fun random(seed: Long?): Sequence<Int> {
        val r = if (seed == null) Random.Default else Random(seed)
        return generateSequence { r.nextInt(min..max) }
     }

    override fun shrinker() = ChooseShrinker(min, max)
  }
}

/**
 * Returns a stream of values, where each value is a
 * Long between the given min and max.
 */
fun Gen.Companion.choose(min: Long, max: Long): Gen<Long> {
  require(min < max) { "min must be < max" }
  return object : Gen<Long> {
     override fun constants(): Iterable<Long> = emptyList()
     override fun random(seed: Long?): Sequence<Long> {
        val r = if (seed == null) Random.Default else Random(seed)
        return generateSequence { r.nextLong(min..max) }
     }
  }
}

/**
 * Returns a stream of values based on weights:
 *
 * Gen.choose(1 to 'A', 2 to 'B') will generate 'A' 33% of the time
 * and 'B' 66% of the time.
 *
 * @throws IllegalArgumentException If any negative weight is given or only
 * weights of zero are given.
 */
fun <T : Any> Gen.Companion.choose(a: Pair<Int, T>, b: Pair<Int, T>, vararg cs: Pair<Int, T>): Gen<T> {
  val allPairs = listOf(a, b) + cs
  val weights = allPairs.map { it.first }
  require(weights.all { it >=0 }) { "Negative weights not allowed" }
  require(weights.any { it > 0 }) { "At least one weight must be greater than zero"}
  return object : Gen<T> {
    // The algorithm for pick is a migration of
    // the algorithm from Haskell QuickCheck
    // http://hackage.haskell.org/package/QuickCheck
    // See function frequency in the package Test.QuickCheck
    private tailrec fun pick(n: Int, l: Sequence<Pair<Int, T>>): T {
      val (w, e) = l.first()
      return if (n <= w) e
      else pick(n - w, l.drop(1))
    }
    override fun constants(): Iterable<T> = emptyList()
    override fun random(seed: Long?): Sequence<T> {
      val r = if (seed == null) Random.Default else Random(seed)
      val total = weights.sum()
      return generateSequence {
        val n = r.nextInt(1, total + 1)
        pick(n, allPairs.asSequence())
      }
    }
  }
}

/**
 * Returns a stream of values, where each value is
 * a pair generated by the underlying generators.
 */
fun <K, V> Gen.Companion.pair(genK: Gen<K>, genV: Gen<V>): Gen<Pair<K, V>> = object : Gen<Pair<K, V>> {
  override fun constants(): Iterable<Pair<K, V>> {
    val keys = genK.constants().toList()
    return keys.zip(genV.random().take(keys.size).toList())
  }

   override fun random(seed: Long?): Sequence<Pair<K, V>> = genK.random(seed).zip(genV.random(seed))
}

/**
 * Returns a stream of values, where each value is
 * a Map, which contains keys and values generated
 * from the underlying generators.
 */
@JvmOverloads
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun <K, V> Gen.Companion.map(genK: Gen<K>, genV: Gen<V>, maxSize: Int = 100): Gen<Map<K, V>> = object : Gen<Map<K, V>> {
  init {
    require(maxSize >= 0) { "maxSize must be positive" }
  }

  override fun constants(): Iterable<Map<K, V>> = emptyList()
   override fun random(seed: Long?): Sequence<Map<K, V>> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence {
         val size = r.nextInt(maxSize)
         genK.random().take(size).zip(genV.random().take(size)).toMap()
      }
   }
}

/**
 * @return a stream of values, where each value is a [Map] of [Pair]s from the given [gen]
 *   the size of the [Map] is bounded between [0, [maxSize])
 */
@JvmOverloads
@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
fun <K, V> Gen.Companion.map(gen: Gen<Pair<K, V>>, maxSize: Int = 100): Gen<Map<K, V>> = object : Gen<Map<K, V>> {
  init {
    require(maxSize >= 0) { "maxSize must be positive" }
  }

  override fun constants(): Iterable<Map<K, V>> = emptyList()
   override fun random(seed: Long?): Sequence<Map<K, V>> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence {
         val size = r.nextInt(maxSize)
         gen.random(seed).take(size).toMap()
      }
   }
}

/**
 * Returns the next pseudorandom, uniformly distributed value
 * from the ASCII range 32-126.
 */
fun Random.nextPrintableChar(): Char = nextInt(from = 32, until = 127).toChar()

/**
 * Generates a [String] of [length] by calling [nextPrintableChar]
 *
 * ```kotlin
 * // Examples
 * val r = Random.Default
 * r.nextPrintableString(-10) // ""
 * r.nextPrintableString(0)   // ""
 * r.nextPrintableString(1)   // " ", "a", "Z", etc.
 * r.nextPrintableString(5)   // "Ha Ha", "gens!", "[{-}]", etc.
 * ```
 *
 * @param length of String
 * @return a given length String of random printable Chars
 */
fun Random.nextPrintableString(length: Int): String {
   return (0 until length).map { nextPrintableChar() }.joinToString("")
}

/**
 * Returns a [[Gen]] which always returns the same value.
 */
fun <T> Gen.Companion.constant(value: T): Gen<T> = object : Gen<T> {
   override fun constants(): Iterable<T> = listOf(value)
   override fun random(seed: Long?): Sequence<T> = generateInfiniteSequence { value }
}

fun Gen.Companion.multiples(k: Int, max: Int): Gen<Int> = object : Gen<Int> {

   // 0 is a multiple of everything
   override fun constants(): Iterable<Int> = listOf(0)

   override fun random(seed: Long?): Sequence<Int> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence {
         r.nextInt(max / k) * k
      }.filter { it >= 0 }
   }
}

fun Gen.Companion.factors(k: Int): Gen<Int> = object : Gen<Int> {

   // 1 is a factor of all ints
   override fun constants(): Iterable<Int> = listOf(1)

   override fun random(seed: Long?): Sequence<Int> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { r.nextInt(k) }.filter { it > 0 }
         .filter { k % it == 0 }
   }
}

/**
 * Returns a [Gen] which returns the sample values in the same order as they are passed in, once all sample values are used
 * it repeats elements from start.
 */
fun <T> Gen.Companion.samples(vararg sampleValues: T) = object : Gen<T> {
    private fun getNextSampleElementProvider(): () -> T  {
        var currentIndex = 0;
        return {
            val nextIndex = currentIndex % sampleValues.size
            val nextValue = sampleValues[nextIndex]
            currentIndex += 1
            nextValue
        }
    }

    override fun random(seed: Long?): Sequence<T> {
        return if(sampleValues.isEmpty()) {
            emptySequence()
        } else {
            generateInfiniteSequence(getNextSampleElementProvider())
        }
    }

    override fun constants(): Iterable<T> = sampleValues.asIterable()
}

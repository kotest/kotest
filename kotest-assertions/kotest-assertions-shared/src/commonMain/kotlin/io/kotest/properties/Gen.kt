package io.kotest.properties

import io.kotest.properties.shrinking.Shrinker
import kotlin.random.Random

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
 * values like zero, minus 1, positive 1, [Int.MAX_VALUE]
 * and [Int.MIN_VALUE].
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
@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
interface Gen<T> {

   companion object

   /**
    * Returns the values that should always be used
    * if this generator is to give complete coverage.
    */
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun constants(): Iterable<T>

   /**
    * Generate a random sequence of type T, that is compatible
    * with the constraints of this generator.
    */
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun random(seed: Long? = null): Sequence<T>

   /**
    * @return the [Shrinker] for this gen or null if shrinking is not supported.
    */
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun shrinker(): Shrinker<T>? = null

   /**
    * Create a new [Gen] by filtering the output of this gen.
    */
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun filter(pred: (T) -> Boolean): Gen<T> {
      val outer = this
      return object : Gen<T> {
         override fun constants(): Iterable<T> = outer.constants().filter(pred)
         override fun random(seed: Long?): Sequence<T> = outer.random(seed).filter(pred)
         override fun shrinker(): Shrinker<T>? {
            val s = outer.shrinker()
            return if (s == null) null else object : Shrinker<T> {
               override fun shrink(failure: T): List<T> = s.shrink(failure).filter(pred)
            }
         }
      }
   }

   /**
    * @return a new [Gen] by filtering this gen's output by the negated function [f]
    */
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun filterNot(f: (T) -> Boolean): Gen<T> = filter { !f(it) }

   /**
    * Create a new [Gen] by mapping the output of this gen.
    */
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun <U> flatMap(f: (T) -> Gen<U>): Gen<U> {
      val outer = this
      return object : Gen<U> {
         override fun constants(): Iterable<U> = outer.constants().flatMap { f(it).constants() }
         override fun random(seed: Long?): Sequence<U> = outer.random(seed).flatMap { f(it).random(seed) }
      }
   }

   /**
    * Create a new [Gen] by mapping the output of this gen.
    */
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun <U> map(f: (T) -> U): Gen<U> {
      val outer = this
      return object : Gen<U> {
         override fun constants(): Iterable<U> = outer.constants().map(f)
         override fun random(seed: Long?): Sequence<U> = outer.random(seed).map(f)
      }
   }

   /**
    * Create a new [Gen] which will return the values of this gen plus null.
    */
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun orNull(): Gen<T?> {
      val outer = this
      return object : Gen<T?> {
         override fun constants(): Iterable<T?> = outer.constants() + listOf(null)
         override fun random(seed: Long?): Sequence<T?> = outer.random(seed)
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
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun <U : T> merge(gen: Gen<U>): Gen<T> {
      val outer = this
      return object : Gen<T> {
         override fun constants(): Iterable<T> = outer.constants() + gen.constants()
         override fun random(seed: Long?): Sequence<T> = outer.random(seed).zip(gen.random(seed)).flatMap {
            sequenceOf(it.first, it.second)
         }
      }
   }

   /**
    * Returns a new [[Gen]] which will return the values from this gen and only once values
    * of this gen exhaust it will return the values from the supplied gen.
    * The supplied gen must be a subtype of the type of this gen.
    */
   @Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
   fun <U : T> concat(gen: Gen<U>): Gen<T> {
      val outer = this
      return object : Gen<T> {
         override fun constants(): Iterable<T> = outer.constants() + gen.constants()
         override fun random(seed: Long?): Sequence<T> = outer.random(seed) + gen.random(seed)
      }
   }
}

/**
 * Create a new [Gen] by keeping only instances of U generated by this gen.
 * This is useful if you have a type hierarchy and only want to retain
 * a particular subtype.
 */
@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
inline fun <T, reified U : T> Gen<T>.filterIsInstance(): Gen<U> {
   val outer = this
   return object : Gen<U> {
      override fun constants(): Iterable<U> = outer.constants().filterIsInstance<U>()
      override fun random(seed: Long?): Sequence<U> = outer.random(seed).filterIsInstance<U>()
   }
}

/**
 * Create a infinite sequence of the given [generator]
 *
 * ```kotlin
 * // Example
 * generateInfiniteSequence { 42 }
 *     .take(3)
 *     .joinToString()
 *     .let { println(it) }
 * // Prints: 42, 42, 42
 * ```
 *
 * @return a new [Sequence] of [T] which always returns the result of [generator]
 */
@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
inline fun <T> generateInfiniteSequence(crossinline generator: () -> T): Sequence<T> =
   Sequence {
      object : Iterator<T> {
         override fun hasNext() = true
         override fun next() = generator()
      }
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
@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
fun <T> Gen<T>.take(amount: Int, seed: Long? = null): List<T> {
   require(amount >= 0) { "Amount must be >= 0, but was $amount" }

   if (amount == 0) return emptyList()

   val generatedValues = (constants() + random(seed).take(amount)).take(amount)
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
@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0. Use Arb.single()")
fun <T> Gen<T>.next(predicate: (T) -> Boolean = { true }, seed: Long?): T {
   return random(seed).first(predicate)
}

/**
 * @return the result of calling [next] with the given [predicate] defaulting seed to `null`
 */
@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
fun <T> Gen<T>.next(predicate: (T) -> Boolean = { true }): T = next(predicate, null)

/**
 * Creates a sequence of unique values from the contents of [random], using [seed] to seed the random function.
 */
@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
fun <T> Gen<T>.uniqueRandoms(seed: Long? = null): Sequence<T> = sequence {
   yieldAll(random(seed).distinct())
}

@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
internal fun getRandomFor(aSeed:Long?):Random {
   return if (aSeed == null) Random.Default else Random(aSeed)
}

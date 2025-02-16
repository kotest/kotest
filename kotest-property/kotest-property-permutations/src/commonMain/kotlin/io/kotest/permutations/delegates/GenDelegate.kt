package io.kotest.permutations.delegates

import io.kotest.permutations.Input
import io.kotest.property.Gen
import io.kotest.property.RTree
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("ClassName")
private object UNINITIALZED_VALUE

/**
 * A [GenDelegate] is the delegated property used in a `permutations` block to return random values.
 *
 * The `gen` function is the entry point to creating the delegate, which accepts a lambda that returns
 * either an [io.kotest.property.Arb] or an [io.kotest.property.Exhaustive] when invoked.
 *
 * For example:
 *
 * ```
 * val a = gen { Arb.int() }
 * ```
 *
 * The first time the property is accessed inside a given iteration of a property test,
 * a new value will be retrieved from the generator. Subsequent reads from the same iteration
 * return the same value. Once an iteration is completed, the value is reset, so the next read
 * in the new iteration will return a new value.
 *
 * If a property test fails, and shrinking is enabled, the delegate will switch to returning
 * values from the shrinking process. In this mode, each time an iteration is completed, the next
 * read will return the next value from the shrink candidates.
 *
 */
class GenDelegate<A>(
   private val gen: Gen<A>,
   private val shouldPrintGeneratedValues: Boolean,
) : ReadOnlyProperty<Any?, A> {

   // the current random value in this iteration as a Sample<A>
   private var _random: Any = UNINITIALZED_VALUE

   // the current shrink candidate in this iteration, as an RTree<A>
   private var _candidate: Any? = UNINITIALZED_VALUE

   // this is the generators infinite sequence for returning random values
   // this will be empty until the delegate is initialized
   private var _samples: Iterator<Sample<A>> = emptyList<Sample<A>>().iterator()

   // when true, shrink candidates are returned instead of random values
   private var _shrinking = false

   // the current candidates for shrinking
   private var _candidates: List<RTree<A>> = mutableListOf()

   // shrinkers might generate duplicate candidates so we must filter previous seen values
   // to avoid infinite loops or slow shrinking
   private val _tested = hashSetOf<A>()

   // contains the property name when accessed so we can use it error output
   private var _property: Any = UNINITIALZED_VALUE

   /**
    * Called once before the first iteration occurs to setup the stream.
    */
   fun initialize(rs: RandomSource) {
      _samples = gen.generate(rs).iterator()
   }

   /**
    * Returns the current value for use in this iteration, obtained from the generators sequence on first read.
    * If we are in shrinking mode, then the value will be from the shrinking process.
    */
   override operator fun getValue(thisRef: Any?, property: KProperty<*>): A {
      if (_property == UNINITIALZED_VALUE) {
         _property = property
      }
      if (_shrinking) {
         @Suppress("UNCHECKED_CAST")
         return (_candidate as RTree<A>).value()
      } else {
         if (_random === UNINITIALZED_VALUE) {
            _random = _samples.next()
            if (shouldPrintGeneratedValues) {
               @Suppress("UNCHECKED_CAST")
               println("Generated value ${property.name} = ${(_random as Sample<A>).value}")
            }
         }
         @Suppress("UNCHECKED_CAST")
         return (_random as Sample<A>).value
      }
   }

   /**
    * Returns the property that this delegate is associated with or null if the delegate was not yet accessed.
    */
   internal fun property(): KProperty<*>? {
      return if (_property == UNINITIALZED_VALUE) null else _property as KProperty<*>
   }

   /**
    * Invoked before a permutation starts and sets the value back to [UNINITIALZED_VALUE],
    * so the next call to [getValue] will return the next random value.
    */
   internal fun reset() {
      _random = UNINITIALZED_VALUE
   }

   /**
    * Enables the shrinking mode for this [GenDelegate] and sets the first level of candidates
    * taken from the last random value.
    *
    * Values now returned by getValue will be from the shrinking process.
    *
    * Instead of [reset] being called after each iteration, the next candidate will be set from [hasNextCandidate].
    */
   internal fun setShrinking() {
      _shrinking = true
      // setup the first level of candidates
      // note: not all generators may have started by the time of the first failure, so we must check
      _candidates = if (_random == UNINITIALZED_VALUE) {
         emptyList()
      } else {
         @Suppress("UNCHECKED_CAST")
         (_random as Sample<A>).shrinks.children.value
      }
   }

   /**
    * Returns the value used in the last iteration.
    */
   fun sample(): Sample<A> {
      @Suppress("UNCHECKED_CAST")
      return _random as Sample<A>
   }

   fun inputs(): Input {
      return Input(property()?.name, sample().value)
   }

   fun candidate(): RTree<A> {
      @Suppress("UNCHECKED_CAST")
      return _candidate as RTree<A>
   }

   /**
    * Sets up the next candidate for shrinking, returning true if there is at least one candidate that
    * has not yet been tested.
    */
   internal fun hasNextCandidate(): Boolean {
      while (_candidates.isNotEmpty()) {
         val next: RTree<A> = _candidates.first()
         _candidates = _candidates.drop(1)
         val value = next.value()
         if (_tested.add(value)) {
            _candidate = next
            return true
         }
      }
      return false
   }

   /**
    * Replaces the current candidates list with the children of the current candidate.
    */
   fun nextCandidates() {
      @Suppress("UNCHECKED_CAST")
      _candidates = (_candidate as RTree<A>).children.value
   }
}

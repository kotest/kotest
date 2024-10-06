package io.kotest.property.core

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.RTree
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private object UNINITIALZED_VALUE

/**
 * A [GenDelegate] is the delegated property used in a `permutations` block to return random values.
 *
 * The `gen` function is the entry point to creating the delegate, which accepts a lambda that returns
 * either an [Arb] or an [Exhaustive] when invoked.
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
   rs: RandomSource,
   gen: Gen<A>,
) : ReadOnlyProperty<Any?, A> {

   // the current random value in this iteration as a Sample<A>
   private var _random: Any = UNINITIALZED_VALUE

   // the current shrink candidate in this iteration, as an RTree<A>
   private var _candidate: Any? = UNINITIALZED_VALUE

   // this is the generators infinite sequence for returning random values
   private var _samples: Iterator<Sample<A>> = gen.generate(rs).iterator()

   // when true, shrink candidates are returned instead of random values
   private var _shrinking = false

   // the current candidates for shrinking
   private var _candidates: List<RTree<A>> = mutableListOf()

   // shrinkers might generate duplicate candidates so we must filter previous seen values
   // to avoid infinite loops or slow shrinking
   private val _tested = hashSetOf<A>()

   // contains the property name when accessed so we can use it in the shrink output
   private var _property: Any = UNINITIALZED_VALUE

   /**
    * Returns the current value for use in this iteration, obtained from the generators sequence on first read.
    * If we are in shrinking mode, then the value will be from the shrinking process.
    */
   override operator fun getValue(thisRef: Any?, property: KProperty<*>): A {
      if (_property == UNINITIALZED_VALUE) {
         _property = property
      }
      if (_shrinking) {
         return (_candidate as RTree<A>).value()
      } else {
         if (_random === UNINITIALZED_VALUE) {
            _random = _samples.next()
         }
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
         (_random as Sample<A>).shrinks.children.value
      }
   }

   /**
    * Returns the value used in the last iteration.
    */
   fun sample(): Sample<A> {
      return _random as Sample<A>
   }

   fun candidate(): RTree<A> {
      return _candidate as RTree<A>
   }

   /**
    * Replaces the current candidates list with the children of the current candidate.
    */
   fun nextCandidates() {
      _candidates = (_candidate as RTree<A>).children.value
   }
}

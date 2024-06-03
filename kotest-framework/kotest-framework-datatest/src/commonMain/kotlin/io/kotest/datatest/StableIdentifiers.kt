package io.kotest.datatest

import io.kotest.common.KotestInternal
import io.kotest.mpp.bestName
import io.kotest.mpp.isStable

/**
 * Used to generate stable identifiers for data tests and to ensure test names are unique.
 *
 * When using an element with data-testing, the generated name must be consistent across different
 * instances of the same element (stable). Otherwise, when using an isolation mode other than the default,
 * the same element will appear under different test names.
 *
 * For example, given a class like this:
 *
 * class Foo(val value: String) {
 *   override fun toString() = Random.nextInt().toString()
 * }
 *
 * If we used the toString() as the test name, between different instances of the same test, the names
 * would not match up, and Kotest would not be able to know if a given element has been executed or not.
 *
 * class MyTest: FunSpec() {
 *    init {
 *       isolationMode = IsolationMode.InstancePerLeaf
 *
 *       context("my data test") {
 *           withData(
 *              Foo("a"),
 *              Foo("b")
 *           ) { a -> a shouldBe a }
 *       }
 *    }
 * }
 *
 * Therefore, to avoid this, data-testing requires data test elements to be stable.
 */
@Deprecated("Internal class, will not be exposed in the future. Deprecated since 5.9")
object StableIdentifiers {

   /**
    * An instance is considered stable if it is a data class where each parameter is either a data class itself,
    * or one of the [io.kotest.mpp.primitiveTypes]. Or if the type of instance is annotated with [io.kotest.datatest.IsStableType].
    *
    * Note: If the user has overridden `toString()` and the returned value is not stable, tests may not appear.
    */
   fun stableIdentifier(t: Any): String {
      return if (isStable(t::class, t)) {
         t.toString()
      } else {
         t::class.bestName()
      }
   }
}

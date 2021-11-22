package io.kotest.core

import io.kotest.core.spec.SpecRef

/**
 * Contains the discovered specs that will be executed.
 *
 * All specs are wrapped in a [SpecRef].
 *
 * On platforms that lack reflective capability, such as nodeJS or native, the specs are
 * either preconstructed or constructed through a simple function. On the JVM, the [KClass]
 * instance is used to reflectively instantiate.
 */
data class TestSuite(val specs: List<SpecRef>) {
   companion object {
      val empty = TestSuite(emptyList())
   }
}

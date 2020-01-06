package io.kotest.specs

import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.StringSpecDsl

/**
 * Decorates a [SpecConfiguration] with the StringSpec DSL.
 */
abstract class StringSpec(body: StringSpec.() -> Unit = {}) : SpecConfiguration(), StringSpecDsl {
   override val addTest = ::addRootTestCase

   init {
      body()
   }
}

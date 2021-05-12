package io.kotest.datatest

import io.kotest.core.test.Identifiers
import io.kotest.mpp.hasAnnotation

fun getStableIdentifier(t: Any): String {
   return when {
      t::class.hasAnnotation<IsStableType>() -> t.toString()
      t is WithDataTestName -> t.dataTestName()
      else -> Identifiers.stableIdentifier(t)
   }
}

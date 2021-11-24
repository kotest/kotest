package io.kotest.datatest

import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.test.Identifiers
import io.kotest.mpp.hasAnnotation

fun getStableIdentifier(t: Any): String {
   return when {
      t::class.hasAnnotation<IsStableType>() || platform != Platform.JVM -> t.toString()
      t is WithDataTestName -> t.dataTestName()
      else -> Identifiers.stableIdentifier(t)
   }
}

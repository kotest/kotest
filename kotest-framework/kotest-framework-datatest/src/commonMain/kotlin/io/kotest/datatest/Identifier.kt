package io.kotest.datatest

import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.mpp.hasAnnotation

fun getStableIdentifier(t: Any?): String {
   return when {
      t == null -> "<null>"
      t::class.hasAnnotation<IsStableType>() || platform != Platform.JVM -> t.toString()
      t is WithDataTestName -> t.dataTestName()
      else ->
         // FIXME Remove deprecation suppression when StableIdentifiers is marked as internal
         @Suppress("DEPRECATION")
         StableIdentifiers.stableIdentifier(t)
   }
}

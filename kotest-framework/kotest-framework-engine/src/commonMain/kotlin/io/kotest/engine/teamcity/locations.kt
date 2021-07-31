package io.kotest.engine.teamcity

import io.kotest.mpp.bestName
import kotlin.reflect.KClass

object Locations {

   fun locationHint(canonicalName: String, lineNumber: Int): String =
      "kotest://$canonicalName:$lineNumber"

   fun locationHint(kclass: KClass<*>): String =
      "kotest://" + kclass.bestName() + ":1"
}

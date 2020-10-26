package io.kotest.core.test

import io.kotest.core.internal.KotestEngineSystemProperties
import io.kotest.mpp.sysprop

enum class TestCaseSeverityLevel(val level: Int) {
   BLOCKER(4),
   CRITICAL(3),
   NORMAL(2),
   MINOR(1),
   TRIVIAL(0);

   fun isEnabled(): Boolean {
      return level >= valueOf(sysprop(KotestEngineSystemProperties.severityPrefix) ?: "TRIVIAL").level
   }
}

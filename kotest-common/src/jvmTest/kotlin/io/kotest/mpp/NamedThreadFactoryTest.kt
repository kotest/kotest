package io.kotest.mpp

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldMatch

class NamedThreadFactoryTest : StringSpec({
   "named thread factory should increment name" {
      val factory = NamedThreadFactory("wibble-%d")
      val t1 = factory.newThread { }
      t1.name.shouldMatch("wibble-\\d+".toRegex())
      val t2 = factory.newThread { }
      t2.name.shouldMatch("wibble-\\d+".toRegex())
   }
})


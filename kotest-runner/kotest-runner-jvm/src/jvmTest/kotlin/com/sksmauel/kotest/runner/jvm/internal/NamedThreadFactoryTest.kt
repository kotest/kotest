package com.sksmauel.kotest.runner.jvm.internal

import io.kotest.runner.jvm.internal.NamedThreadFactory
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

class NamedThreadFactoryTest : StringSpec({
  "named thread factory should increment name" {
    val factory = NamedThreadFactory("wibble-%d")
    val t1 = factory.newThread { }
    t1.name shouldBe "wibble-0"
    val t2 = factory.newThread { }
    t2.name shouldBe "wibble-1"
  }
})
package com.sksamuel.kotlintest.runner.jvm

import io.kotlintest.runner.jvm.internal.NamedThreadFactory
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class NamedThreadFactoryTest : StringSpec({
  "named thread factory should increment name" {
    val factory = NamedThreadFactory("wibble-%d")
    val t1 = factory.newThread { }
    t1.name shouldBe "wibble-0"
    val t2 = factory.newThread { }
    t2.name shouldBe "wibble-1"
  }
})
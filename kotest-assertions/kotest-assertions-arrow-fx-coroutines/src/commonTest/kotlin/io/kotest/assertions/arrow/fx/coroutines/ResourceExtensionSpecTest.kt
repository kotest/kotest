package io.kotest.assertions.arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec

class ResourceExtensionSpecTest : FunSpec({

  val sub = ResourceUnderTest()

  val res = install(sub.asResource().extension()) {
    configure()
  }

  test("acquired but not released") {
    res.get() shouldBe sub
    sub.isOpen shouldBe true
    sub.isConfigured shouldBe true
  }

  afterSpec {
    sub.isReleased shouldBe true
    shouldThrow<IllegalStateException> {
      res.get()
    }
  }
})

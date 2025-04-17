package io.kotest.assertions.arrow.fx.coroutines

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.DescribeSpec

class ResourceExtensionLeafTest : DescribeSpec({
  val sub = ResourceUnderTest()

  install(ResourceExtension(sub.asResource(), LifecycleMode.Leaf)) {
    configure()
  }

  describe("context") {
    sub.isOpen shouldBe false
    sub.isConfigured shouldBe false
    sub.count shouldBe 0

    it("should initialize per leaf") {
      sub.isOpen shouldBe true
      sub.isConfigured shouldBe true
      sub.count shouldBe 1
    }

    it("this test should have a new container") {
      sub.isConfigured shouldBe true
      sub.count shouldBe 2
    }
  }
})

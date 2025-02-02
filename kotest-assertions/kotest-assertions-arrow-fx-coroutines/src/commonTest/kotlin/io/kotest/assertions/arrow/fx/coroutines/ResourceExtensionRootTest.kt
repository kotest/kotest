package io.kotest.assertions.arrow.fx.coroutines

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec

class ResourceExtensionRootTest : FunSpec({
  val sub = ResourceUnderTest()
  
  install(ResourceExtension(sub.asResource(), LifecycleMode.Root)) {
    configure()
  }
  
  beforeSpec {
    sub.count shouldBe 0
    sub.isConfigured shouldBe false
    sub.isOpen shouldBe false
    sub.isReleased shouldBe false
  }
  
  context("initializes once for container") {
    test("should initialize per root") {
      sub.count shouldBe 1
      sub.isConfigured shouldBe true
      sub.isOpen shouldBe true
      sub.isReleased shouldBe false
    }
    
    test("should not have re-initialized") {
      sub.count shouldBe 1
    }
  }
  
  test("this root test should have a different container") {
    sub.count shouldBe 2
    sub.isConfigured shouldBe true
    sub.isOpen shouldBe true
    sub.isReleased shouldBe false
  }
})

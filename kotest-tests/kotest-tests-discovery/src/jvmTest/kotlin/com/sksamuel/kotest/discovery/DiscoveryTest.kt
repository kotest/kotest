package com.sksamuel.kotest.discovery

import io.kotest.core.spec.FunSpec
import io.kotest.core.spec.StringSpec
import io.kotest.runner.jvm.DiscoveryRequest
import io.kotest.runner.jvm.TestDiscovery
import io.kotest.shouldBe

class MyFunSpec : FunSpec()
class MyStringSpec : StringSpec()
private class PrivateSpec : FunSpec()
private class InternalSpec : StringSpec()

class DiscoveryTest : FunSpec({
   test("should detect public spec classes") {
      TestDiscovery.discover(DiscoveryRequest()).specs.map { it.simpleName }.toSet() shouldBe setOf(
         "DiscoveryTest",
         "MyFunSpec",
         "MyStringSpec",
         "FakeSpecConfiguration"
      )
   }
})

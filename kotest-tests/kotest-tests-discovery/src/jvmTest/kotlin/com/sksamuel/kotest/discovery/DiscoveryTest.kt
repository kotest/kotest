package com.sksamuel.kotest.discovery

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.runner.jvm.DiscoveryRequest
import io.kotest.runner.jvm.TestDiscovery
import io.kotest.shouldBe

class MyFunSpec : FunSpec()
class MyStringSpec : StringSpec()
private class PrivateSpec : FunSpec()
internal class InternalSpec : WordSpec()

class DiscoveryTest : FunSpec({
   test("should detect public spec classes") {
      TestDiscovery.discover(DiscoveryRequest()).specs.map { it.simpleName }.toSet() shouldBe setOf(
         "DiscoveryTest", // this test
         "MyFunSpec", // public
         "MyStringSpec" // public
      )
   }
})

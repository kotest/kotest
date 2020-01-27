package com.sksamuel.kotest.discovery

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.jvm.DiscoveryRequest
import io.kotest.runner.jvm.TestDiscovery

class MyFunSpec : FunSpec()
class MyStringSpec : StringSpec()
private class PrivateSpec : FunSpec()
internal class InternalSpec : WordSpec()

class DiscoveryTest : FunSpec({

   test("should detect only public spec classes") {
      TestDiscovery.discover(DiscoveryRequest(allowInternal = false)).specs.map { it.simpleName }.toSet() shouldBe setOf(
         "DiscoveryTest", // this test
         "MyFunSpec", // public
         "MyStringSpec" // public
      )
   }

   test("should detect internal spec classes") {
      TestDiscovery.discover(DiscoveryRequest(allowInternal = true)).specs.map { it.simpleName }.toSet() shouldBe setOf(
         "DiscoveryTest", // this test
         "MyFunSpec", // public
         "MyStringSpec",
         "InternalSpec"
      )
   }
})

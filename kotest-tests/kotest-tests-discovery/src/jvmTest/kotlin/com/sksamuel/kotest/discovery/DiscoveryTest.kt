package com.sksamuel.kotest.discovery

import io.kotest.core.spec.FunSpec
import io.kotest.core.spec.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.runner.jvm.DiscoveryRequest
import io.kotest.runner.jvm.TestDiscovery
import io.kotest.specs.FreeSpec

class MyFunSpec : FunSpec()
class MyStringSpec : StringSpec()
private class PrivateSpec : FunSpec()
internal class InternalSpec : StringSpec()

class DiscoveryTest : FreeSpec({
   "should detect public spec classes" {
      TestDiscovery.discover(DiscoveryRequest()).specs.shouldHaveSize(3)
   }
})

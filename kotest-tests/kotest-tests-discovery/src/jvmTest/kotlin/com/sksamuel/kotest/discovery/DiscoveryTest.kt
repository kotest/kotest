package com.sksamuel.kotest.discovery

import io.kotest.core.spec.FunSpec
import io.kotest.core.spec.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.runner.jvm.DiscoveryRequest
import io.kotest.runner.jvm.TestDiscovery

private class MyFunSpec : FunSpec()
private class MyStringSpec : StringSpec()
private class PrivateSpec : FunSpec()
private class InternalSpec : StringSpec()

class DiscoveryTest : FunSpec({
   test("should detect public spec classes") {
      TestDiscovery.discover(DiscoveryRequest()).specs.shouldHaveSize(3)
   }
})

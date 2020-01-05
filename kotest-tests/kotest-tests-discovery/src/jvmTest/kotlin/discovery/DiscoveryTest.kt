package discovery

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.runner.jvm.DiscoveryRequest
import io.kotest.runner.jvm.TestDiscovery
import io.kotest.specs.FunSpec

class DiscoveryTest : FunSpec() {
   init {
      test("discovery should pick up functions and classes") {
         val req = DiscoveryRequest()
         val result = TestDiscovery.discover(req)
         result.containers.shouldHaveSize(2)
      }
   }
}

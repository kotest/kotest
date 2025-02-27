package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import redis.clients.jedis.JedisPool

class TestContainerExtensionSpecTest : FunSpec() {
   init {

      val container = install(TestContainerExtension("redis:5.0.3-alpine", LifecycleMode.Spec)) {
         startupAttempts = 2
         withExposedPorts(6379)
      }

      val jedis = JedisPool(container.host, container.firstMappedPort)

      test("should be initialized in the spec") {
         jedis.resource.set("foo", "bar")
         jedis.resource.get("foo") shouldBe "bar"
      }

      test("this test should share the container") {
         jedis.resource.get("foo") shouldBe "bar"
      }
   }
}

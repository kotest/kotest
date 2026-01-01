package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.GenericContainer
import redis.clients.jedis.JedisPool

class TestContainerSpecExtensionTest : FunSpec() {
   init {

      val container = install(TestContainerSpecExtension(GenericContainer("redis:5.0.3-alpine"))) {
         startupAttempts = 2
         withExposedPorts(6379)
      }

      val jedis = JedisPool(container.host, container.firstMappedPort)

      test("should be initialized in the spec") {
         jedis.resource["foo"] = "bar"
         jedis.resource["foo"] shouldBe "bar"
      }

      test("this test should share the container") {
         jedis.resource["foo"] shouldBe "bar"
      }

      afterProject {
         container.isRunning shouldBe false
      }
   }
}

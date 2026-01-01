package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.GenericContainer
import redis.clients.jedis.JedisPool

private val container = GenericContainer("redis:5.0.3-alpine").apply {
   startupAttempts = 2
   withExposedPorts(6379)
}

@Order(1)
class TestContainerProjectExtensionTest1 : FunSpec() {
   init {

      val container = install(TestContainerProjectExtension(container))
      val jedis = JedisPool(container.host, container.firstMappedPort)

      test("should be initialized in the spec") {
         jedis.resource["foo"] = "bar"
         jedis.resource["foo"] shouldBe "bar"
      }

      test("this test should share the container") {
         jedis.resource["foo"] shouldBe "bar"
      }
   }
}

@Order(2)
class TestContainerProjectExtensionTest2 : FunSpec() {
   init {

      val container = install(TestContainerProjectExtension(container))
      val jedis = JedisPool(container.host, container.firstMappedPort)

      test("this test should share the container from the earlier test") {
         jedis.resource["foo"] shouldBe "bar"
      }
   }
}

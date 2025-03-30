//package io.kotest.extensions.testcontainers
//
//import io.kotest.core.extensions.install
//import io.kotest.core.spec.style.DescribeSpec
//import io.kotest.matchers.shouldBe
//import redis.clients.jedis.JedisPool
//
//class TestContainerExtensionLeafTest : DescribeSpec() {
//   init {
//
//      val container = install(TestContainerExtension("redis:5.0.3-alpine", LifecycleMode.Leaf)) {
//         startupAttempts = 1
//         withExposedPorts(6379)
//      }
//
//      describe("context") {
//         container.isRunning shouldBe false
//         it("should initialize per leaf") {
//            val jedis = JedisPool(container.host, container.firstMappedPort)
//            jedis.resource.set("foo", "bar")
//            jedis.resource.get("foo") shouldBe "bar"
//         }
//
//         it("this test should have a new container") {
//            val jedis = JedisPool(container.host, container.firstMappedPort)
//            jedis.resource.get("foo") shouldBe null
//         }
//      }
//   }
//}

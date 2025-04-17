//package io.kotest.extensions.testcontainers
//
//import io.kotest.core.extensions.install
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.shouldBe
//import redis.clients.jedis.JedisPool
//
//@Deprecated("to be removed")
//class TestContainerExtensionRootTest : FunSpec() {
//   init {
//
//      val container = install(TestContainerExtension("redis:5.0.3-alpine", LifecycleMode.Root)) {
//         startupAttempts = 2
//         withExposedPorts(6379)
//      }
//
//      test("should initialize per root") {
//         val jedis = JedisPool(container.host, container.firstMappedPort)
//         jedis.resource.set("foo", "bar")
//         jedis.resource.get("foo") shouldBe "bar"
//      }
//
//      test("this root test should have a different container") {
//         val jedis = JedisPool(container.host, container.firstMappedPort)
//         jedis.resource.get("foo") shouldBe null
//      }
//   }
//}

package com.sksamuel.kotest.engine.metadata

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.MetadataKey
import io.kotest.core.test.config.TestConfig
import io.kotest.matchers.shouldBe

class MetadataResolverTest : FunSpec({

   val Issue = MetadataKey<String>("Issue")
   val Owner = MetadataKey<String>("Owner")

   test("spec-level metadata is stored in _metadata") {
      val spec = object : FunSpec({
         metadata[Issue] = "JIRA-123"
         metadata[Owner] = "payments-team"

         test("dummy") {}
      }) {}

      spec._metadata[Issue] shouldBe "JIRA-123"
      spec._metadata[Owner] shouldBe "payments-team"
   }

   test("test-level metadata via config uses immutable Map") {
      val config = TestConfig(metadata = mapOf(Issue to "JIRA-456"))
      config.metadata[Issue] shouldBe "JIRA-456"
   }

   test("test config with metadata passed via mapOf") {
      val spec = object : FunSpec({
         test("inner").config(
            metadata = mapOf(Issue to "test-issue")
         ) {}
      }) {}

      val tests = spec.tests()
      tests.size shouldBe 1

      val testConfig = tests.first().config
      testConfig?.metadata?.get(Issue) shouldBe "test-issue"
   }
})

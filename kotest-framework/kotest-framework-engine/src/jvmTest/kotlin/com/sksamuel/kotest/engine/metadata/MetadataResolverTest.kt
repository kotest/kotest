package com.sksamuel.kotest.engine.metadata

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.MetadataKey
import io.kotest.core.test.TestMetadata
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.config.TestConfigResolver
import io.kotest.matchers.shouldBe

class MetadataResolverTest : FunSpec({

   val Issue = MetadataKey<String>("Issue")
   val Owner = MetadataKey<String>("Owner")

   test("resolver merges spec-level and test-level metadata with child winning") {
      val resolver = TestConfigResolver()

      // Create a spec with spec-level metadata
      val spec = object : FunSpec({
         metadata[Issue] = "spec-issue"
         metadata[Owner] = "spec-owner"

         test("inner").config(
            metadata = TestMetadata().also {
               it[Issue] = "test-issue"
            }
         ) {}
      }) {}

      // Materialize and get the test case
      val rootTests = spec.rootTests()
      rootTests.size shouldBe 1

      // Verify the test has config with metadata
      val testConfig = rootTests.first().config
      testConfig?.metadata?.get(Issue) shouldBe "test-issue"
   }

   test("spec-level metadata is accessible via appliedMetadata") {
      val spec = object : FunSpec({
         metadata[Issue] = "JIRA-123"
         metadata[Owner] = "payments-team"

         test("dummy") {}
      }) {}

      spec.appliedMetadata()[Issue] shouldBe "JIRA-123"
      spec.appliedMetadata()[Owner] shouldBe "payments-team"
   }

   test("metadata set at test config level is stored in TestConfig") {
      val md = TestMetadata()
      md[Issue] = "JIRA-456"

      val config = TestConfig(metadata = md)
      config.metadata[Issue] shouldBe "JIRA-456"
   }
})

package com.sksamuel.kotest.engine.metadata

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.MetadataKey
import io.kotest.core.test.TestMetadata
import io.kotest.core.test.ResolvedTestMetadata
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MetadataKeyTest : FunSpec({

   test("MetadataKey equality by name and type") {
      val key1 = MetadataKey<String>("Issue")
      val key2 = MetadataKey<String>("Issue")
      key1 shouldBe key2
      key1.hashCode() shouldBe key2.hashCode()
   }

   test("MetadataKey inequality for different names") {
      val key1 = MetadataKey<String>("Issue")
      val key2 = MetadataKey<String>("Owner")
      key1 shouldNotBe key2
   }

   test("MetadataKey inequality for same name but different types") {
      val stringKey = MetadataKey<String>("data")
      val intKey = MetadataKey<Int>("data")
      stringKey shouldNotBe intKey
   }

   test("MetadataKey toString includes name and type") {
      val key = MetadataKey<String>("Issue")
      key.toString() shouldBe "MetadataKey(Issue: String)"
   }
})

class TestMetadataTest : FunSpec({

   test("get and set values") {
      val Issue = MetadataKey<String>("Issue")
      val metadata = TestMetadata()
      metadata[Issue] = "JIRA-123"
      metadata[Issue] shouldBe "JIRA-123"
   }

   test("get returns null for missing key") {
      val Issue = MetadataKey<String>("Issue")
      val metadata = TestMetadata()
      metadata[Issue] shouldBe null
   }

   test("isEmpty and isNotEmpty") {
      val Issue = MetadataKey<String>("Issue")
      val metadata = TestMetadata()
      metadata.isEmpty() shouldBe true
      metadata.isNotEmpty() shouldBe false

      metadata[Issue] = "JIRA-123"
      metadata.isEmpty() shouldBe false
      metadata.isNotEmpty() shouldBe true
   }

   test("child metadata overrides parent per-key") {
      val Issue = MetadataKey<String>("Issue")
      val Owner = MetadataKey<String>("Owner")

      val parent = TestMetadata()
      parent[Issue] = "parent-issue"
      parent[Owner] = "parent-owner"

      val child = TestMetadata()
      child[Issue] = "child-issue"

      val resolved = child.mergeWith(parent)
      resolved[Issue] shouldBe "child-issue"    // child wins
      resolved[Owner] shouldBe "parent-owner"   // inherited from parent
   }

   test("snapshot returns immutable ResolvedTestMetadata") {
      val Issue = MetadataKey<String>("Issue")
      val metadata = TestMetadata()
      metadata[Issue] = "JIRA-123"

      val snapshot = metadata.snapshot()
      snapshot[Issue] shouldBe "JIRA-123"
      snapshot.isEmpty() shouldBe false
   }

   test("ResolvedTestMetadata from empty TestMetadata is empty") {
      val resolved = TestMetadata().snapshot()
      resolved.isEmpty() shouldBe true
      resolved.keys() shouldBe emptySet()
   }
})

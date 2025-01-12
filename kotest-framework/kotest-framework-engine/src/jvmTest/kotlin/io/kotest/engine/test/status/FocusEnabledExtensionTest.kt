package io.kotest.engine.test.status

import io.kotest.core.descriptors.append
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.matchers.shouldBe

class FocusEnabledExtensionTest : FunSpec() {
   init {
      val tc = TestCase(
         FocusEnabledExtensionTest::class.toDescriptor().append("t"),
         TestNameBuilder.builder("t").build(),
         FocusEnabledTest(),
         { },
         sourceRef(),
         TestType.Test,
         parent = null,
      )
      test("root focused test should always be enabled") {
         FocusEnabledExtension
            .isEnabled(tc.copy(name = TestNameBuilder.builder("f: t").build())) shouldBe Enabled.enabled
      }
      test("nested tests should always be enabled") {
         val tc2 = TestCase(
            FocusEnabledExtensionTest::class.toDescriptor().append("t"),
            TestNameBuilder.builder("t").build(),
            FocusEnabledExtensionTest(),
            { },
            sourceRef(),
            TestType.Test,
            parent = tc,
         )
         FocusEnabledExtension.isEnabled(tc2) shouldBe Enabled.enabled
      }
   }
}

class FocusEnabledTest : FunSpec() {
   init {
      test("f: focused test") { }
      test("non focused test") {
         error("boom") // this shouldn't happen because this test should be ignored as there is another focused test
      }
   }
}

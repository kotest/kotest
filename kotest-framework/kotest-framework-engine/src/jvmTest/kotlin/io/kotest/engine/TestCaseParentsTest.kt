package io.kotest.engine

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.parents
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class TestCaseParentsTest : FunSpec() {
   init {
      test("test case parents") {

         val tc1 = TestCase(
            TestCaseParentsTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            TestCaseParentsTest(),
            {},
            SourceRef.None,
            TestType.Test
         )

         val tc2 = TestCase(
            TestCaseParentsTest::class.toDescriptor().append("foo").append("bar"),
            TestNameBuilder.builder("bar").build(),
            TestCaseParentsTest(),
            {},
            SourceRef.None,
            TestType.Test,
            parent = tc1
         )

         val tc3 = TestCase(
            TestCaseParentsTest::class.toDescriptor().append("foo").append("bar").append("baz"),
            TestNameBuilder.builder("baz").build(),
            TestCaseParentsTest(),
            {},
            SourceRef.None,
            TestType.Test,
            parent = tc2
         )


         tc1.parents().shouldBeEmpty()
         tc2.parents() shouldBe listOf(tc1)
         tc3.parents() shouldBe listOf(tc1, tc2)
      }
   }
}

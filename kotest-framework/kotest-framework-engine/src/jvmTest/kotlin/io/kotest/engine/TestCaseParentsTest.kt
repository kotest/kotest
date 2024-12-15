package io.kotest.engine

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.parents
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class TestCaseParentsTest : FunSpec() {
   init {
      test("test case parents") {

         val tc1 = TestCase(
            TestCaseParentsTest::class.toDescriptor().append("foo"),
            TestName("foo"),
            TestCaseParentsTest(),
            {},
            sourceRef(),
            TestType.Test
         )

         val tc2 = TestCase(
            TestCaseParentsTest::class.toDescriptor().append("foo").append("bar"),
            TestName("bar"),
            TestCaseParentsTest(),
            {},
            sourceRef(),
            TestType.Test,
            parent = tc1
         )

         val tc3 = TestCase(
            TestCaseParentsTest::class.toDescriptor().append("foo").append("bar").append("baz"),
            TestName("baz"),
            TestCaseParentsTest(),
            {},
            sourceRef(),
            TestType.Test,
            parent = tc2
         )


         tc1.parents().shouldBeEmpty()
         tc2.parents() shouldBe listOf(tc1)
         tc3.parents() shouldBe listOf(tc1, tc2)
      }
   }
}

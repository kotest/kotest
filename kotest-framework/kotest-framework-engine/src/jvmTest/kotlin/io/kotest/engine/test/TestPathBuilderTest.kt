package io.kotest.engine.test

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.descriptors.TestPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.path.TestPathBuilder
import io.kotest.matchers.shouldBe

class TestPathBuilderTest : FunSpec({

   test("line breaks are removed and trimmed") {
      TestPathBuilder.builder().withTest(
         """  A test case
        with a line break  """
      ).build() shouldBe TestPath("A test case with a line break")
   }

   test("line breaks are removed from a builder with a spec") {
      TestPathBuilder.builder().withSpec(TestPathBuilderTest::class).withTest(
         """  A test case
        with a line break  """
      ).build() shouldBe TestPath("TestPathBuilderTest/A test case with a line break")
   }

   test("test names should be trimmed") {
      TestPathBuilder.builder().withTest("  spaces  ").build() shouldBe TestPath("spaces")
   }

   test("multiple test names should be trimmed") {
      TestPathBuilder.builder()
         .withTest("  spaces  ")
         .withTest("  another test  ").build() shouldBe TestPath("spaces -- another test")
   }

   test("builder should error if adding spec after tests") {
      shouldThrowAny {
         TestPathBuilder.builder()
            .withTest("a")
            .withSpec(TestPathBuilderTest::class)
      }
   }
})

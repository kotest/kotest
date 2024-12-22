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
      ).build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/A test case with a line break")
   }

   test("test names should be trimmed") {
      TestPathBuilder.builder().withTest("  spaces  ").build() shouldBe TestPath("spaces")
   }

   test("trim new lines") {
      TestPathBuilder.builder().withTest("  trim new lines \n  ").build() shouldBe TestPath("trim new lines")
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

   test("escape all characters") {
      TestPathBuilder.builder()
         .withTest(" \$foobar \\\$barfoo \\n ")
         .build() shouldBe TestPath("\$foobar \\\$barfoo \\n")
   }

   test("test path should include spec and use spec separator") {
      TestPathBuilder.builder()
         .withSpec(TestPathBuilderTest::class)
         .withTest("a context")
         .build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/a context")
   }

   test("test path should include parent tests and use test path separator") {
      TestPathBuilder.builder()
         .withSpec(TestPathBuilderTest::class)
         .withTest("a context")
         .withTest("nested test")
         .build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/a context -- nested test")
   }
})

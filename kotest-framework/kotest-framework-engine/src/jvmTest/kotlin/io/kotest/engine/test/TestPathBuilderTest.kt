package io.kotest.engine.test

import io.kotest.core.descriptors.TestPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.path.TestPathBuilder
import io.kotest.matchers.shouldBe

class TestPathBuilderTest : FunSpec({

   test("test path should include spec and use spec separator") {
      TestPathBuilder.builder<TestPathBuilderTest>()
         .withTest("a context")
         .build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/a context")
   }

   test("line breaks are removed and trimmed") {
      TestPathBuilder.builder<TestPathBuilderTest>().withTest(
         """  A test case
        with a line break  """
      ).build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/A test case with a line break")
   }

   test("line breaks are removed from multiple tests") {
      TestPathBuilder.builder<TestPathBuilderTest>()
         .withTest(
            """  A test case
        with a line break  """
         ).withTest(
            """
         another
         multi
         line
         """
         ).build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/A test case with a line break -- another multi line")
   }

   test("test names should be trimmed") {
      TestPathBuilder.builder<TestPathBuilderTest>().withTest("  spaces  ")
         .build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/spaces")
   }

   test("trim new lines") {
      TestPathBuilder.builder<TestPathBuilderTest>().withTest("  trim new lines \n  ")
         .build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/trim new lines")
   }

   test("multiple test names should be trimmed") {
      TestPathBuilder.builder<TestPathBuilderTest>()
         .withTest("  spaces  ")
         .withTest("  another test  ")
         .build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/spaces -- another test")
   }

   test("escape all characters") {
      TestPathBuilder.builder<TestPathBuilderTest>()
         .withTest(" \$foobar \\\$barfoo \\n ")
         .build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/\$foobar \\\$barfoo \\n")
   }

   test("test path should include parent tests and use test path separator") {
      TestPathBuilder.builder<TestPathBuilderTest>()
         .withTest("a context")
         .withTest("nested test")
         .build() shouldBe TestPath("io.kotest.engine.test.TestPathBuilderTest/a context -- nested test")
   }
})

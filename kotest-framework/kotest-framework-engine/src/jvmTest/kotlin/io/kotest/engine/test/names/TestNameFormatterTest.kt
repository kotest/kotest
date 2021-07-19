package io.kotest.engine.test.names

import io.kotest.core.plan.TestName
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestNameFormatter
import io.kotest.matchers.shouldBe

class TestNameFormatterTest : FunSpec({
   test("test name with affixes") {

      TestNameFormatter(true, false, false).format(TestName("foo", "", false, false, "a", "b")) shouldBe ReportName("a")

      TestNameFormatter(true, false, false).format(
         TestName(
            "foo",
            "",
            false,
            false,
            null,
            "b"
         )
      ) shouldBe ReportName("a")
      TestNameFormatter(true, false, false).format(
         TestName(
            "foo",
            "",
            false,
            false,
            "a",
            null
         )
      ) shouldBe ReportName("a")
   }

   test("test name with tags") {
   }

   test("test name with focus bang") {
      TestNameFormatter(true, false, false).format(
         TestName(
            "foo",
            "",
            false,
            true,
            null,
            null
         )
      ) shouldBe ReportName("a")
      TestNameFormatter(true, false, false).format(
         TestName(
            "foo",
            "",
            true,
            false,
            null,
            null
         )
      ) shouldBe ReportName("a")
   }

   test("test name with all") {}
})

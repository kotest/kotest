package io.kotest.core.test.names

import io.kotest.core.names.TestName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestNameTest : FunSpec({

   test("parse simple name") {
      TestName("foo") shouldBe TestName("foo", false, false, null, null, false)
   }

   test("parse focus") {
      TestName("f:foo") shouldBe TestName("foo", true, false, null, null, false)
   }

   test("parse bang") {
      TestName("!foo") shouldBe TestName("foo", false, true, null, null, false)
      TestName("!!foo") shouldBe TestName("!foo", false, true, null, null, false)
   }

   test("parse with suffix") {
      TestName(null, "!foo", "s", true) shouldBe TestName("foo", false, true, null, "s", true)
   }

   test("parse with whitespace") {
      TestName("    !foo") shouldBe TestName("foo", false, true, null, null, false)
   }

   test("parse with prefix") {
      TestName("p", "foo", null, true) shouldBe TestName("foo", false, false, "p", null, true)
   }
})

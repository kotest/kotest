package io.kotest.engine.test.names

import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestNameBuilderTest : FunSpec({

   test("parse simple name") {
      TestNameBuilder.Companion.builder("foo").build() shouldBe TestName("foo", false, false, null, null, false)
   }

   test("parse focus") {
      TestNameBuilder.Companion.builder("f:foo").build() shouldBe TestName("foo", true, false, null, null, false)
   }

   test("parse bang") {
      TestNameBuilder.Companion.builder("!foo").build() shouldBe TestName("foo", false, true, null, null, false)
      TestNameBuilder.Companion.builder("!!foo").build() shouldBe TestName("!foo", false, true, null, null, false)
   }

   test("parse with suffix") {
      TestNameBuilder.Companion.builder("!foo").withSuffix("s").withDefaultAffixes().build() shouldBe TestName(
         "foo",
         false,
         true,
         null,
         "s",
         true
      )
   }

   test("parse with whitespace") {
      TestNameBuilder.Companion.builder("    !foo").build() shouldBe TestName("foo", false, true, null, null, false)
   }

   test("parse with prefix") {
      TestNameBuilder.Companion.builder("foo").withPrefix("p").withDefaultAffixes().build() shouldBe TestName(
         "foo",
         false,
         false,
         "p",
         null,
         true
      )
   }
})

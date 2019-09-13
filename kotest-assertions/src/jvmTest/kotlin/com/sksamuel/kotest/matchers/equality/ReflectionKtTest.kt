package com.sksamuel.kotest.matchers.equality

import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.FunSpec

class ReflectionKtTest : FunSpec() {

  data class Foo(val a: String, val b: Int, val c: Boolean)

  init {

    test("shouldBeEqualToUsingFields") {
      Foo("sammy", 1, true).shouldBeEqualToUsingFields(Foo("sammy", 1, false), Foo::a, Foo::b)
      Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("sammy", 345435, false), Foo::a)
      Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("sammy", 345435, true), Foo::a, Foo::c)
      Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("sammy", 345435, true), Foo::c, Foo::a)
    }

    test("shouldBeEqualToUsingFields failure message") {

      shouldThrow<AssertionError> {
        Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("sammy", 345435, false), Foo::a, Foo::c)
      }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=sammy, b=345435, c=false) using fields [a, c]; Failed for [c: true != false]"

      shouldThrow<AssertionError> {
        Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("stef", 13, false), Foo::a, Foo::c)
      }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=stef, b=13, c=false) using fields [a, c]; Failed for [a: sammy != stef, c: true != false]"
    }

    test("shouldBeEqualToIgnoringFields") {
      Foo("sammy", 1, true).shouldBeEqualToIgnoringFields(Foo("sammy", 1, false), Foo::c)
      Foo("sammy", 13, true).shouldBeEqualToIgnoringFields(Foo("sammy", 345435, false), Foo::b, Foo::c)
      Foo("sammy", 13, true).shouldBeEqualToIgnoringFields(Foo("sammy", 345435, true), Foo::b)
    }

    test("shouldBeEqualToIgnoringFields failure message") {

      shouldThrow<AssertionError> {
        Foo("sammy", 13, true).shouldBeEqualToIgnoringFields(Foo("sammy", 345435, false), Foo::a, Foo::b)
      }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=sammy, b=345435, c=false) ignoring fields [a, b]; Failed for [c: true != false]"

      shouldThrow<AssertionError> {
        Foo("sammy", 13, true).shouldBeEqualToIgnoringFields(Foo("stef", 13, false), Foo::c)
      }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=stef, b=13, c=false) ignoring fields [c]; Failed for [a: sammy != stef]"
    }
  }
}
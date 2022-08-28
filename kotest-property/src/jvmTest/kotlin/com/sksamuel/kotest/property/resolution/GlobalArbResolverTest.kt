package com.sksamuel.kotest.property.resolution

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.checkAll
import io.kotest.property.resolution.GlobalArbResolver

class GlobalArbResolverTest : FunSpec() {
   init {

      data class MyType(val value: String)

      val arb = arbitrary { MyType("foo") }
      GlobalArbResolver.register<MyType>(arb)

      test("should detect global default mapping arity 1") {
         checkAll<MyType>() { it shouldBe MyType("foo") }
      }

      test("should detect global default mapping arity 2") {
         checkAll<MyType, MyType>() { a, b ->
            a shouldBe MyType("foo")
            b shouldBe MyType("foo")
         }
      }

      test("should detect global default mapping arity 3") {
         checkAll<MyType, MyType, MyType>() { a, b, c ->
            a shouldBe MyType("foo")
            b shouldBe MyType("foo")
            c shouldBe MyType("foo")
         }
      }

      test("should detect global default mapping arity 4") {
         checkAll<MyType, MyType, MyType, MyType>() { a, b, c, d ->
            a shouldBe MyType("foo")
            b shouldBe MyType("foo")
            c shouldBe MyType("foo")
            d shouldBe MyType("foo")
         }
      }

      test("should detect global default mapping arity 5") {
         checkAll<MyType, MyType, MyType, MyType, MyType>() { a, b, c, d, e ->
            a shouldBe MyType("foo")
            b shouldBe MyType("foo")
            c shouldBe MyType("foo")
            d shouldBe MyType("foo")
            e shouldBe MyType("foo")
         }
      }
   }
}

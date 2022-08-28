package com.sksamuel.kotest.property.resolution

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.checkAll
import io.kotest.property.resolution.GlobalArbResolver
import io.kotest.property.resolution.NoGeneratorFoundException
import kotlin.reflect.typeOf

class GlobalArbResolverTest : FunSpec() {
   init {

      data class MyType(val value: String)

      val arb = arbitrary { MyType("foo") }
      GlobalArbResolver.register<MyType>(arb)

      test("should detect global default mapping arity 1") {
         checkAll<MyType>() { it shouldBe MyType("foo") }
      }

      test("should detect global default mapping arity 2") {
         val arb = arbitrary { MyType("foo") }
         checkAll<MyType, MyType>() { a, b ->
            a shouldBe MyType("foo")
            b shouldBe MyType("foo")
         }
      }

      test("should detect global default mapping arity 3") {
         val arb = arbitrary { MyType("foo") }
         checkAll<MyType, MyType, MyType>() { a, b, c ->
            a shouldBe MyType("foo")
            b shouldBe MyType("foo")
            c shouldBe MyType("foo")
         }
      }

      test("should detect global default mapping arity 4") {
         val arb = arbitrary { MyType("foo") }
         checkAll<MyType, MyType, MyType, MyType>() { a, b, c, d ->
            a shouldBe MyType("foo")
            b shouldBe MyType("foo")
            c shouldBe MyType("foo")
            d shouldBe MyType("foo")
         }
      }

      test("should detect global default mapping arity 5") {
         val arb = arbitrary { MyType("foo") }
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

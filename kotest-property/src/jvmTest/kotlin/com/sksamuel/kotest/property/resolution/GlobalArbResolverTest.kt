package com.sksamuel.kotest.property.resolution

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.resolution.GlobalArbResolver
import java.net.URI

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

      test("should be used in Reflective Arbs") {
         val arbUri = arbitrary {
            URI.create(
               Arb.string().filter("[a-zA-Z][\\p{Alnum}+.-]*".toRegex()::matches).bind() + "://" + Arb.string()
                  .filter("[\\w-.&/]+".toRegex()::matches).bind()
            )
         }
         GlobalArbResolver.register<URI>(arbUri)
         data class NestedUri(val uri: URI)
         Arb.bind<NestedUri>().next() shouldNot beNull()
      }
   }
}

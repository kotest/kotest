package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.file.shouldEqualSpecifiedJson
import io.kotest.assertions.json.file.shouldEqualSpecifiedJsonIgnoringOrder
import io.kotest.assertions.json.paths.shouldEqualSpecifiedJson
import io.kotest.assertions.json.paths.shouldEqualSpecifiedJsonIgnoringOrder
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.assertions.json.shouldEqualSpecifiedJsonIgnoringOrder
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class EqualIgnoringUnknownFileTest : FunSpec({
   test("extra field in actual - passes") {
      checkAll(Arb.string(1..10, Codepoint.az())) { string ->
         val a = """ { "a" : "$string", "b": "bar" } """
         val b = """ { "a" : "$string" }"""

         withJsonTestFile(a) shouldEqualSpecifiedJson b
         withJsonTestFile(a).toPath() shouldEqualSpecifiedJson b
      }
   }

   test("expected field has different value - should fail") {
      val a = """ { "a" : "foo", "b" : "bar" } """
      val b = """ { "a" : "foo", "b" : "baz" } """

      withJsonTestFile(a) shouldEqualSpecifiedJson a
      withJsonTestFile(a).toPath() shouldEqualSpecifiedJson a

      shouldFail {
         withJsonTestFile(a) shouldEqualSpecifiedJson b
      }.shouldHaveMessage(
         """
            At 'b' expected 'baz' but was 'bar'

            expected:<{
              "a": "foo",
              "b": "baz"
            }> but was:<{
              "a": "foo",
              "b": "bar"
            }>
         """.trimIndent()
      )

      shouldFail {
         withJsonTestFile(a).toPath() shouldEqualSpecifiedJson b
      }.shouldHaveMessage(
         """
            At 'b' expected 'baz' but was 'bar'

            expected:<{
              "a": "foo",
              "b": "baz"
            }> but was:<{
              "a": "foo",
              "b": "bar"
            }>
         """.trimIndent()
      )
   }

   test("actual missing field") {
      val a = """ { "a" : "foo" } """
      val b = """ { "a" : "foo", "b": "bar" } """

      shouldFail {
         withJsonTestFile(a) shouldEqualSpecifiedJson b
      }.shouldHaveMessage(
         """
            The top level object was missing expected field(s) [b]

            expected:<{
              "a": "foo",
              "b": "bar"
            }> but was:<{
              "a": "foo"
            }>
         """.trimIndent()
      )

      shouldFail {
         withJsonTestFile(a).toPath() shouldEqualSpecifiedJson b
      }.shouldHaveMessage(
         """
            The top level object was missing expected field(s) [b]

            expected:<{
              "a": "foo",
              "b": "bar"
            }> but was:<{
              "a": "foo"
            }>
         """.trimIndent()
      )
   }

   context("Nested object") {
      test("extra field is ok") {
         checkAll(Arb.string(1..10, Codepoint.az())) { string ->
            val a = """ { "wrapper": { "a" : "$string", "b": "bar" } }"""
            val b = """ { "wrapper": { "a" : "$string" } }"""

            withJsonTestFile(a) shouldEqualSpecifiedJson b
            withJsonTestFile(a).toPath() shouldEqualSpecifiedJson b
         }
      }

      test("nested expected value differs") {
         val a = """ { "wrapper": { "a" : "foo", "b": "bar" } }"""
         val b = """ { "wrapper": { "a" : "foo", "b": "baz" } }"""

         withJsonTestFile(a) shouldEqualSpecifiedJson a
         withJsonTestFile(a).toPath() shouldEqualSpecifiedJson a

         shouldFail {
            withJsonTestFile(a) shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """
               At 'wrapper.b' expected 'baz' but was 'bar'

               expected:<{
                 "wrapper": {
                   "a": "foo",
                   "b": "baz"
                 }
               }> but was:<{
                 "wrapper": {
                   "a": "foo",
                   "b": "bar"
                 }
               }>
            """.trimIndent()
         )

         shouldFail {
            withJsonTestFile(a).toPath() shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """
               At 'wrapper.b' expected 'baz' but was 'bar'

               expected:<{
                 "wrapper": {
                   "a": "foo",
                   "b": "baz"
                 }
               }> but was:<{
                 "wrapper": {
                   "a": "foo",
                   "b": "bar"
                 }
               }>
            """.trimIndent()
         )
      }

      test("actual missing field") {
         val a = """ { "wrapper": { "a" : "foo" } } """
         val b = """ { "wrapper": { "a" : "foo", "b": "bar" } } """

         shouldFail {
            withJsonTestFile(a) shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """
               At 'wrapper' object was missing expected field(s) [b]

               expected:<{
                 "wrapper": {
                   "a": "foo",
                   "b": "bar"
                 }
               }> but was:<{
                 "wrapper": {
                   "a": "foo"
                 }
               }>
            """.trimIndent()
         )

         shouldFail {
            withJsonTestFile(a).toPath() shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """
               At 'wrapper' object was missing expected field(s) [b]

               expected:<{
                 "wrapper": {
                   "a": "foo",
                   "b": "bar"
                 }
               }> but was:<{
                 "wrapper": {
                   "a": "foo"
                 }
               }>
            """.trimIndent()
         )
      }

   }

   context("Arrays") {
      test("extra field is ok") {
         checkAll(Arb.string(1..10, Codepoint.az())) { string ->
            val a = """ { "wrapper": [{ "a" : "$string", "b": "bar" }] }"""
            val b = """ { "wrapper": [{ "a" : "$string" }] }"""

            withJsonTestFile(a) shouldEqualSpecifiedJson b
            withJsonTestFile(a).toPath() shouldEqualSpecifiedJson b
         }
      }

      test("nested expected value differs") {
         val a = """ { "wrapper": [{ "a" : "foo", "b": "bar" }] }"""
         val b = """ { "wrapper": [{ "a" : "foo", "b": "baz" }] }"""

         withJsonTestFile(a) shouldEqualSpecifiedJson a
         withJsonTestFile(a).toPath() shouldEqualSpecifiedJson a

         shouldFail {
            withJsonTestFile(a) shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """
               At 'wrapper.[0].b' expected 'baz' but was 'bar'

               expected:<{
                 "wrapper": [
                   {
                     "a": "foo",
                     "b": "baz"
                   }
                 ]
               }> but was:<{
                 "wrapper": [
                   {
                     "a": "foo",
                     "b": "bar"
                   }
                 ]
               }>
            """.trimIndent()
         )

         shouldFail {
            withJsonTestFile(a).toPath() shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """
               At 'wrapper.[0].b' expected 'baz' but was 'bar'

               expected:<{
                 "wrapper": [
                   {
                     "a": "foo",
                     "b": "baz"
                   }
                 ]
               }> but was:<{
                 "wrapper": [
                   {
                     "a": "foo",
                     "b": "bar"
                   }
                 ]
               }>
            """.trimIndent()
         )
      }

      test("actual missing field") {
         val a = """ { "wrapper": [ { "a" : "foo" } ] } """
         val b = """ { "wrapper": [ { "a" : "foo", "b": "bar" } ] } """

         shouldFail {
            withJsonTestFile(a) shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """
               At 'wrapper.[0]' object was missing expected field(s) [b]

               expected:<{
                 "wrapper": [
                   {
                     "a": "foo",
                     "b": "bar"
                   }
                 ]
               }> but was:<{
                 "wrapper": [
                   {
                     "a": "foo"
                   }
                 ]
               }>
            """.trimIndent()
         )

         shouldFail {
            withJsonTestFile(a).toPath() shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """
               At 'wrapper.[0]' object was missing expected field(s) [b]

               expected:<{
                 "wrapper": [
                   {
                     "a": "foo",
                     "b": "bar"
                   }
                 ]
               }> but was:<{
                 "wrapper": [
                   {
                     "a": "foo"
                   }
                 ]
               }>
            """.trimIndent()
         )
      }

      test("extra fields ok with different order array") {
         val a = """ { "wrapper": [{ "c" : "baz" }, { "a" : "foo", "b": "bar" } ] }"""
         val b = """ { "wrapper": [{ "a" : "foo" }, { "c" : "baz" } ] }"""

         withJsonTestFile(a) shouldEqualSpecifiedJsonIgnoringOrder b
         withJsonTestFile(a).toPath() shouldEqualSpecifiedJsonIgnoringOrder b
      }
   }
})

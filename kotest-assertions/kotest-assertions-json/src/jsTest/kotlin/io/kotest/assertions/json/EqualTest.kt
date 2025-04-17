@file:Suppress("DEPRECATION") // FIXME remove deprecation suppression when io.kotest.assertions.json.JsonMatchersKt.shouldMatchJson is removed

package io.kotest.assertions.json

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.boolean

class EqualTest : FunSpec() {
   init {

      test("comparing strings in objects") {

         checkAll(Arb.string(1..10, Codepoint.az())) { string ->
            val a = """ { "a" : "$string" } """
            a shouldEqualJson a
         }

         val a = """ { "a": "foo", "b": "bar" } """
         val b = """ { "a": "foo", "b": "baz" } """
         a shouldEqualJson a
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected 'baz' but was 'bar'

expected:
{
  "a": "foo",
  "b": "baz"
}

actual:
{
  "a": "foo",
  "b": "bar"
}"""
         )
      }

      test("comparing boolean in objects") {
         checkAll(Exhaustive.boolean(), Exhaustive.boolean()) { a, b ->
            val json = """ { "a" : $a, "b": $b } """
            json shouldEqualJson json
         }
         val a = """ { "a": true, "b": false } """
         val b = """ { "a": true, "b": true } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected true but was false

expected:
{
  "a": true,
  "b": true
}

actual:
{
  "a": true,
  "b": false
}"""
         )
      }

      test("comparing long in objects") {

         checkAll<Long> { long ->
            val a = """ { "a" : $long } """
            a shouldEqualJson a
         }

         val a = """ { "a": 123, "b": 354 } """
         val b = """ { "a": 123, "b": 326 } """
         a shouldEqualJson a
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected 326 but was 354

expected:
{
  "a": 123,
  "b": 326
}

actual:
{
  "a": 123,
  "b": 354
}"""
         )
      }

      test("comparing doubles in objects") {

         checkAll(Arb.numericDouble()) { double ->
            val a = """ { "a" : $double } """
            a shouldEqualJson a
         }

         val a = """ { "a" : 123, "b": 354 } """
         val b = """ { "a" : 123, "b" : 9897 } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected 9897 but was 354

expected:
{
  "a": 123,
  "b": 9897
}

actual:
{
  "a": 123,
  "b": 354
}"""
         )
      }

      test("comparing object with extra key") {
         val a = """ { "a" : "foo", "b" : "bar", "c": "baz" } """
         val b = """ { "a" : "foo", "b" : "bar" } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """The top level object was missing expected field(s) [c]

expected:
{
  "a": "foo",
  "b": "bar"
}

actual:
{
  "a": "foo",
  "b": "bar",
  "c": "baz"
}"""
         )
      }

      test("comparing objects with differing keys") {
         val a = """ { "a" : "foo", "b" : "bar" } """
         val b = """ { "a" : "foo", "c" : "bar" } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """The top level object was missing expected field(s) [c]

expected:
{
  "a": "foo",
  "c": "bar"
}

actual:
{
  "a": "foo",
  "b": "bar"
}"""
         )
      }

      test("comparing object with extra keys") {
         val a = """ { "a" : "foo", "b" : "bar", "c": "baz", "d": true } """
         val b = """ { "a" : "foo", "b" : "bar" } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """The top level object was missing expected field(s) [c,d]

expected:
{
  "a": "foo",
  "b": "bar"
}

actual:
{
  "a": "foo",
  "b": "bar",
  "c": "baz",
  "d": true
}"""
         )
      }

      test("comparing object with missing key") {
         val a = """ { "a" : "foo", "b" : "bar" } """
         val b = """ { "a" : "foo", "b" : "bar", "c": "baz" } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """The top level object has extra field(s) [c]

expected:
{
  "a": "foo",
  "b": "bar",
  "c": "baz"
}

actual:
{
  "a": "foo",
  "b": "bar"
}"""
         )
      }

      test("comparing object with missing keys") {
         val a = """ { "a" : "foo", "b" : "bar" } """
         val b = """ { "a" : "foo", "b" : "bar", "c": "baz", "d": 123 } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """The top level object has extra field(s) [c,d]

expected:
{
  "a": "foo",
  "b": "bar",
  "c": "baz",
  "d": 123
}

actual:
{
  "a": "foo",
  "b": "bar"
}"""
         )
      }


      test("comparing boolean to string with TypeCoercion.Enabled") {
         val a = """ { "a" : "foo", "b" : true } """
         val b = """ { "a" : "foo", "b" : "true" } """
         a.shouldEqualJson {
            typeCoercion = TypeCoercion.Enabled
            b
         }

         val c = """ { "a" : "foo", "b" : false } """
         val d = """ { "a" : "foo", "b" : "false" } """
         c.shouldEqualJson {
            typeCoercion = TypeCoercion.Enabled
            d
         }
      }

      test("comparing long to string with TypeCoercion.Enabled") {
         val a = """ { "a" : "foo", "b" : 123 } """
         val b = """ { "a" : "foo", "b" : "123" } """
         a.shouldEqualJson {
            typeCoercion = TypeCoercion.Enabled
            b
         }
      }

      test("comparing double to string with TypeCoercion.Enabled") {
         val a = """ { "a" : "foo", "b" : 12.45 } """
         val b = """ { "a" : "foo", "b" : "12.45" } """
         a.shouldEqualJson {
            typeCoercion = TypeCoercion.Enabled
            b
         }
      }

      test("comparing string to long with TypeCoercion.Enabled") {
         val a = """ { "a" : "foo", "b" : "12" } """
         val b = """ { "a" : "foo", "b" : 12 } """
         a.shouldEqualJson {
            typeCoercion = TypeCoercion.Enabled
            b
         }
      }

      test("comparing string to boolean with TypeCoercion.Enabled") {
         val a = """ { "a" : "foo", "b" : "true" } """
         val b = """ { "a" : "foo", "b" : true } """
         a.shouldEqualJson {
            typeCoercion = TypeCoercion.Enabled
            b
         }

         val c = """ { "a" : "foo", "b" : "false" } """
         val d = """ { "a" : "foo", "b" : false } """
         c.shouldEqualJson {
            typeCoercion = TypeCoercion.Enabled
            d
         }
      }

      test("comparing string to double with TypeCoercion.Enabled") {
         val a = """ { "a" : "foo", "b" : "12.45" } """
         val b = """ { "a" : "foo", "b" : 12.45 } """
         a.shouldEqualJson {
            typeCoercion = TypeCoercion.Enabled
            b
         }
      }

      test("comparing string to null") {
         val a = """ { "a" : "foo", "b" : "bar" } """
         val b = """ { "a" : "foo", "b" : null } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected null but was string

expected:
{
  "a": "foo",
  "b": null
}

actual:
{
  "a": "foo",
  "b": "bar"
}"""
         )
      }

      test("comparing boolean to null") {
         val a = """ { "a" : "foo", "b" : true } """
         val b = """ { "a" : "foo", "b" : null } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected null but was boolean

expected:
{
  "a": "foo",
  "b": null
}

actual:
{
  "a": "foo",
  "b": true
}"""
         )
      }

      test("comparing int to null") {
         val a = """ { "a" : "foo", "b" : 234 } """
         val b = """ { "a" : "foo", "b" : null } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected null but was int

expected:
{
  "a": "foo",
  "b": null
}

actual:
{
  "a": "foo",
  "b": 234
}"""
         )
      }

      test("comparing double to null") {
         val a = """ { "a" : "foo", "b" : 12.34 } """
         val b = """ { "a" : "foo", "b" : null } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected null but was double

expected:
{
  "a": "foo",
  "b": null
}

actual:
{
  "a": "foo",
  "b": 12.34
}"""
         )
      }

      test("comparing string to object") {
         val a = """ { "a" : "foo", "b" : "bar" } """
         val b = """ { "a" : "foo", "b" : { "c": true } } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected object type but was string

expected:
{
  "a": "foo",
  "b": {
    "c": true
  }
}

actual:
{
  "a": "foo",
  "b": "bar"
}"""
         )
      }

      test("comparing boolean to object") {
         val a = """ { "a" : "foo", "b" : true } """
         val b = """ { "a" : "foo", "b" : { "c": true } } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected object type but was boolean

expected:
{
  "a": "foo",
  "b": {
    "c": true
  }
}

actual:
{
  "a": "foo",
  "b": true
}"""
         )
      }

      test("comparing double to object") {
         val a = """ { "a" : "foo", "b" : 12.45 } """
         val b = """ { "a" : "foo", "b" : { "c": true } } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected object type but was double

expected:
{
  "a": "foo",
  "b": {
    "c": true
  }
}

actual:
{
  "a": "foo",
  "b": 12.45
}"""
         )
      }

      test("comparing array to object") {
         val a = """ { "a" : "foo", "b" : [1,2,3] } """
         val b = """ { "a" : "foo", "b" : { "c": true } } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected object type but was array

expected:
{
  "a": "foo",
  "b": {
    "c": true
  }
}

actual:
{
  "a": "foo",
  "b": [
    1,
    2,
    3
  ]
}"""
         )
      }

      test("comparing object to boolean") {
         val a = """ { "a" : "foo", "b" : { "c": true } } """
         val b = """ { "a" : "foo", "b" : true } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected boolean but was object

expected:
{
  "a": "foo",
  "b": true
}

actual:
{
  "a": "foo",
  "b": {
    "c": true
  }
}"""
         )
      }

      test("comparing object to long") {
         val a = """ { "a" : "foo", "b" : { "c": true } } """
         val b = """ { "a" : "foo", "b" : 2067120338512882656 } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected long but was object

expected:
{
  "a": "foo",
  "b": 2067120338512882656
}

actual:
{
  "a": "foo",
  "b": {
    "c": true
  }
}"""
         )
      }

      test("comparing array to string") {
         val a = """ { "a" : "foo", "b" : { "c": true } } """
         val b = """ { "a" : "foo", "b" : "werqe" } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b' expected string but was object

expected:
{
  "a": "foo",
  "b": "werqe"
}

actual:
{
  "a": "foo",
  "b": {
    "c": true
  }
}"""
         )
      }

      test("deep comparison of objects should show full path to error") {
         val a = """ { "a" : "foo", "b" : { "c": { "d": 123 } } } """
         val b = """ { "a" : "foo", "b" : { "c": { "d": 534 } } } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b.c.d' expected 534 but was 123

expected:
{
  "a": "foo",
  "b": {
    "c": {
      "d": 534
    }
  }
}

actual:
{
  "a": "foo",
  "b": {
    "c": {
      "d": 123
    }
  }
}"""
         )
      }

      test("deep comparison of arrays should show full path to error") {
         val a = """ { "a" : "foo", "b" : { "c": { "d": [1,2,3] } } } """
         val b = """ { "a" : "foo", "b" : { "c": { "d": [1,2,4] } } } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b.c.d.[2]' expected 4 but was 3

expected:
{
  "a": "foo",
  "b": {
    "c": {
      "d": [
        1,
        2,
        4
      ]
    }
  }
}

actual:
{
  "a": "foo",
  "b": {
    "c": {
      "d": [
        1,
        2,
        3
      ]
    }
  }
}"""
         )
      }

      test("comparing arrays of different length") {
         val a = """ { "a" : "foo", "b" : { "c": { "d": [1,2,3,4] } } } """
         val b = """ { "a" : "foo", "b" : { "c": { "d": [1,2,4] } } } """
         shouldFail {
            a shouldEqualJson b
         }.shouldHaveMessage(
            """At 'b.c.d' expected array length 3 but was 4

expected:
{
  "a": "foo",
  "b": {
    "c": {
      "d": [
        1,
        2,
        4
      ]
    }
  }
}

actual:
{
  "a": "foo",
  "b": {
    "c": {
      "d": [
        1,
        2,
        3,
        4
      ]
    }
  }
}"""
         )
      }

      test("key order should use PropertyOrder.Strict") {
         val a = """
            {
               "id": 32672932069455,
               "title": "Default Title",
               "sku": "RIND-TOTEO-001-MCF",
               "requires_shipping": true,
               "taxable": true,
               "featured_image": null
            }
            """

         val b = """
            {
               "sku": "RIND-TOTEO-001-MCF",
               "id": 32672932069455,
               "title": "Default Title",
               "requires_shipping": true,
               "taxable": true,
               "featured_image": null
            }
            """
         a.shouldEqualJson(b)
         shouldFail {
            a.shouldEqualJson {
               propertyOrder = PropertyOrder.Strict
               b
            }
         }.shouldHaveMessage(
            """The top level object expected field 0 to be 'sku' but was 'id'

expected:
{
  "sku": "RIND-TOTEO-001-MCF",
  "id": 32672932069455,
  "title": "Default Title",
  "requires_shipping": true,
  "taxable": true,
  "featured_image": null
}

actual:
{
  "id": 32672932069455,
  "title": "Default Title",
  "sku": "RIND-TOTEO-001-MCF",
  "requires_shipping": true,
  "taxable": true,
  "featured_image": null
}"""
         )
      }
   }
}

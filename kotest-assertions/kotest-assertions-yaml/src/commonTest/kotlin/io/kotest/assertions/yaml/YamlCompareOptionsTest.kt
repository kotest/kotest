package io.kotest.assertions.yaml

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain

class YamlCompareOptionsTest : FunSpec({

   infix fun String.shouldEqualYamlIgnoringArrayOrder(other: String) = this shouldEqualYaml {
      arrayOrder = ArrayOrder.Lenient
      other
   }

   context("array order") {

      test("default options require the same array order") {
         val e = shouldFail {
            """
               items:
               - 1
               - 2
            """.trimIndent() shouldEqualYaml """
               items:
               - 2
               - 1
            """.trimIndent()
         }
         e.message shouldContain "At 'items.[0]' expected '2' but was '1'"
         e.message shouldContain "At 'items.[1]' expected '1' but was '2'"
      }

      test("ArrayOrder.Lenient allows arrays to be shuffled") {
         """
            items:
            - 1
            - 2
         """.trimIndent() shouldEqualYamlIgnoringArrayOrder """
            items:
            - 2
            - 1
         """.trimIndent()
      }

      test("ArrayOrder.Lenient handles duplicates") {
         """
            items:
            - 1
            - 2
            - 2
         """.trimIndent() shouldEqualYamlIgnoringArrayOrder """
            items:
            - 2
            - 1
            - 2
         """.trimIndent()
      }

      test("ArrayOrder.Lenient matches nested mappings regardless of order") {
         """
            users:
            - name: sam
              age: 30
            - name: bob
              age: 40
         """.trimIndent() shouldEqualYamlIgnoringArrayOrder """
            users:
            - age: 40
              name: bob
            - age: 30
              name: sam
         """.trimIndent()
      }

      test("ArrayOrder.Lenient reports elements that cannot be matched") {
         val e = shouldFail {
            """
               items:
               - 1
               - 2
               - 2
            """.trimIndent() shouldEqualYamlIgnoringArrayOrder """
               items:
               - 1
               - 2
               - 0
            """.trimIndent()
         }
         e.message shouldContain "At 'items.[2]' has extra element '2' not found (or too few) in '[1, 2, 0]'"
      }

      test("ArrayOrder.Lenient still requires the same array length") {
         val e = shouldFail {
            """
               items:
               - 1
            """.trimIndent() shouldEqualYamlIgnoringArrayOrder """
               items:
               - 1
               - 1
            """.trimIndent()
         }
         e.message shouldContain "At 'items' expected array length 2 but was 1"
      }
   }

   context("field comparison") {

      test("default options reject missing fields") {
         val e = shouldFail {
            """
               a: 1
            """.trimIndent() shouldEqualYaml """
               a: 1
               b: 2
            """.trimIndent()
         }
         e.message shouldContain "object was missing expected field(s) [b]"
      }

      test("FieldComparison.Lenient allows extra fields in the actual document") {
         """
            a: 1
            b: 2
         """.trimIndent() shouldEqualYaml {
            fieldComparison = FieldComparison.Lenient
            """
               a: 1
            """.trimIndent()
         }
      }

      test("FieldComparison.Lenient still requires expected fields to be present") {
         val e = shouldFail {
            """
               a: 1
            """.trimIndent() shouldEqualYaml {
               fieldComparison = FieldComparison.Lenient
               """
                  a: 1
                  b: 2
               """.trimIndent()
            }
         }
         e.message shouldContain "object was missing expected field(s) [b]"
      }
   }

   context("property order") {

      test("PropertyOrder.Strict requires properties in the same order") {
         """
            a: 1
            b: 2
         """.trimIndent() should equalYaml(
            """
               a: 1
               b: 2
            """.trimIndent(),
            yamlCompareOptions { propertyOrder = PropertyOrder.Strict }
         )

         val e = shouldFail {
            """
               a: 1
               b: 2
            """.trimIndent() should equalYaml(
               """
                  b: 9
                  a: 1
               """.trimIndent(),
               yamlCompareOptions { propertyOrder = PropertyOrder.Strict }
            )
         }
         e.message shouldContain "object expected field 0 to be 'b' but was 'a'"
         e.message shouldContain "object expected field 1 to be 'a' but was 'b'"
         e.message shouldContain "At 'b' expected '9' but was '2'"
      }

      test("PropertyOrder.Lenient is the default") {
         """
            a: 1
            b: 2
         """.trimIndent() shouldEqualYaml """
            b: 2
            a: 1
         """.trimIndent()
      }
   }

   context("number format") {

      test("NumberFormat.Strict is the default and compares contents verbatim") {
         val e = shouldFail {
            """
               value: 1.0
            """.trimIndent() shouldEqualYaml """
               value: 1.00
            """.trimIndent()
         }
         e.message shouldContain "At 'value' expected '1.00' but was '1.0'"

         val e2 = shouldFail {
            """
               value: 1E2
            """.trimIndent() shouldEqualYaml """
               value: 100
            """.trimIndent()
         }
         e2.message shouldContain "At 'value' expected '100' but was '1E2'"
      }

      test("NumberFormat.Lenient compares parsed values") {
         """
            value: 1.0
         """.trimIndent() shouldEqualYaml {
            numberFormat = NumberFormat.Lenient
            """
               value: 1.00
            """.trimIndent()
         }

         """
            value: 1E2
         """.trimIndent() shouldEqualYaml {
            numberFormat = NumberFormat.Lenient
            """
               value: 100
            """.trimIndent()
         }
      }

      test("NumberFormat.Strict requires identical formatting") {
         val e = shouldFail {
            """
               value: 1.0
            """.trimIndent() should equalYaml(
               """
                  value: 1.00
               """.trimIndent(),
               yamlCompareOptions { numberFormat = NumberFormat.Strict }
            )
         }
         e.message shouldContain "At 'value' expected '1.00' but was '1.0'"
      }

      test("YAML-specific numeric forms are compared literally") {
         val e = shouldFail {
            """
               value: 0x1F
            """.trimIndent() shouldEqualYaml """
               value: 31
            """.trimIndent()
         }
         e.message shouldContain "At 'value' expected '31' but was '0x1F'"
      }
   }

   context("tags") {

      test("same tags on both sides are equal") {
         """
            value: !!str 5
         """.trimIndent() shouldEqualYaml """
            value: !!str 5
         """.trimIndent()
      }

      test("a tag present on one side only is a difference") {
         val e = shouldFail {
            """
               value: !!str 5
            """.trimIndent() shouldEqualYaml """
               value: 5
            """.trimIndent()
         }
         e.message shouldContain "At 'value' expected tag '<none>' but was 'tag:yaml.org,2002:str'"
      }

      test("different tags are a difference") {
         val e = shouldFail {
            """
               value: !!str 5
            """.trimIndent() shouldEqualYaml """
               value: !!int 5
            """.trimIndent()
         }
         e.message shouldContain "At 'value' expected tag 'tag:yaml.org,2002:int' but was 'tag:yaml.org,2002:str'"
      }

      test("tag mismatches are reported at the nested path") {
         val e = shouldFail {
            """
               outer:
                 inner: !!str 5
            """.trimIndent() shouldEqualYaml """
               outer:
                 inner: 5
            """.trimIndent()
         }
         e.message shouldContain "At 'outer.inner' expected tag '<none>' but was 'tag:yaml.org,2002:str'"
      }
   }

   context("types and nulls") {

      test("reports incompatible types") {
         val e = shouldFail {
            """
               value: [1, 2]
            """.trimIndent() shouldEqualYaml """
               value: hello
            """.trimIndent()
         }
         e.message shouldContain "At 'value' expected scalar but was list"
      }

      test("reports expected null mismatches") {
         val e = shouldFail {
            """
               value: null
            """.trimIndent() shouldEqualYaml """
               value: hello
            """.trimIndent()
         }
         e.message shouldContain "At 'value' expected scalar but was null"
      }

      test("reports scalar expected but null actual") {
         val e = shouldFail {
            """
               value: hello
            """.trimIndent() shouldEqualYaml """
               value: null
            """.trimIndent()
         }
         e.message shouldContain "At 'value' expected null but was scalar"
      }

      test("nulls are considered equal") {
         """
            value: null
         """.trimIndent() shouldEqualYaml """
            value: ~
         """.trimIndent()
      }
   }

   context("negated and direct matcher forms") {

      test("shouldNotEqualYaml with options reports not-matching when lenient options would match") {
         val e = shouldFail {
            """
               a: 1
               b: 2
            """.trimIndent() shouldNotEqualYaml {
               fieldComparison = FieldComparison.Lenient
               """
                  a: 1
               """.trimIndent()
            }
         }
         e.message shouldContain "Expected values to not match"
      }

      test("shouldNotEqualYaml with options passes when documents differ") {
         """
            a: 1
         """.trimIndent() shouldNotEqualYaml {
            fieldComparison = FieldComparison.Lenient
            """
               a: 2
            """.trimIndent()
         }
      }

      test("equalYaml accepts explicit options") {
         """
            items:
            - 2
            - 1
         """.trimIndent() should equalYaml(
            """
               items:
               - 1
               - 2
            """.trimIndent(),
            yamlCompareOptions { arrayOrder = ArrayOrder.Lenient }
         )
      }

      test("quoted and unquoted scalars with the same content remain equal") {
         """
            key: "value"
         """.trimIndent() shouldEqualYaml """
            key: value
         """.trimIndent()
      }
   }
})

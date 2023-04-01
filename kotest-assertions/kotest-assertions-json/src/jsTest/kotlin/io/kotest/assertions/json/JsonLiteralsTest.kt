//package io.kotest.assertions.json
//
//import io.kotest.assertions.shouldFail
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.string.shouldContain
//import io.kotest.matchers.throwable.shouldHaveMessage
//
//class JsonLiteralsTest : FunSpec(
//   {
//      test("Unsupported type") {
//         shouldThrow<IllegalArgumentException> {
//            "0x03" shouldEqualJson "0x03"
//         }.shouldHaveMessage("Unsupported kotlinx-serialization type 0x03")
//      }
//
//      context("Strict (default) comparisons") {
//         test("comparing float and int") {
//            shouldFail {
//               "3.2" shouldEqualJson "3"
//            }.shouldHaveMessage(
//               """
//               The top level expected 3 but was 3.2
//
//               expected:
//               3
//
//               actual:
//               3.2
//            """.trimIndent()
//            )
//         }
//
//         test("quoted numbers are treated as strings") {
//            shouldFail { "\"1E3\"" shouldEqualJson "1000.0" }
//            // Unquoted 1E3 is parsed to double and back due to prettifying output
//            shouldFail { "\"1000.0\"" shouldEqualJson "1E3" }.message shouldContain
//               "The top level expected number but was string"
//            shouldFail { "10.0" shouldEqualJson "\"10.0\"" }.message shouldContain
//               "The top level expected string but was number"
//         }
//
//         test("comparing exponent-based float with regular float") {
//            "1E3" shouldEqualJson "1000.0"
//            "1000.0" shouldEqualJson "1E3"
//            "1000.0" shouldEqualJson "1000"
//            "5E0" shouldEqualJson "5.0"
//            "2E-1" shouldEqualJson "0.2"
//         }
//
//         test("comparing high-precision floating point numbers") {
//
//            // Note: In the middle paragraph of the failure message the expected JSON has been
//            //       formatted as a JSON tree using KotlinX.serialization which parses the
//            //       number to a double and back, hence the loss of precision.
//
//            shouldFail {
//               "0.12345678912345678" shouldEqualJson "0.123456789123456789"
//            }.shouldHaveMessage(
//               """
//               The top level expected 0.123456789123456789 but was 0.12345678912345678
//
//               expected:
//               0.123456789123456789
//
//               actual:
//               0.12345678912345678
//            """.trimIndent()
//            )
//         }
//
//         test("comparing string and boolean") {
//            shouldFail {
//               "true" shouldEqualJson "\"true\""
//            }.shouldHaveMessage(
//               """
//               The top level expected string but was boolean
//
//               expected:
//               "true"
//
//               actual:
//               true
//            """.trimIndent()
//            )
//         }
//      }
//
//      context("CompareMode.Exact requires same format for numbers") {
//         infix fun String.shouldExactlyEqualJson(expected: String) =
//            this.shouldEqualJson(expected, compareJsonOptions { numberFormat = NumberFormat.Strict })
//
//         test("comparing float and exponent") {
//            shouldFail {
//               "10.0" shouldExactlyEqualJson "1e1"
//            }.shouldHaveMessage(
//               """
//               The top level expected 1e1 but was 10.0
//
//               expected:
//               1e1
//
//               actual:
//               10.0
//            """.trimIndent()
//            )
//         }
//
//         test("comparing int and exponent") {
//            shouldFail {
//               "10" shouldExactlyEqualJson "1e1"
//            }.shouldHaveMessage(
//               """
//               The top level expected 1e1 but was 10
//
//               expected:
//               1e1
//
//               actual:
//               10
//            """.trimIndent()
//            )
//         }
//
//         test("comparing float and int") {
//            shouldFail {
//               "10.0" shouldExactlyEqualJson "10"
//            }.shouldHaveMessage(
//               """
//               The top level expected 10 but was 10.0
//
//               expected:
//               10
//
//               actual:
//               10.0
//            """.trimIndent()
//            )
//         }
//
//         test("quoted numbers are treated as strings") {
//            shouldFail { "\"1E3\"" shouldEqualJson "1000.0" }
//            // Unquoted 1E3 is parsed to double and back due to prettifying output
//            shouldFail { "\"1000.0\"" shouldEqualJson "1E3" }.message shouldContain
//               "The top level expected number but was string"
//            shouldFail { "10.0" shouldEqualJson "\"10.0\"" }.message shouldContain
//               "The top level expected string but was number"
//         }
//      }
//
//      context("Lenient type-conversions") {
//
//         infix fun String.lenientShouldEqualJson(expected: String) =
//            this.shouldEqualJson(expected, compareJsonOptions { typeCoercion = TypeCoercion.Enabled })
//
//         test("comparing exponent-based float with regular float") {
//            "1E3" lenientShouldEqualJson "\"1000.0\""
//            "1000.0" lenientShouldEqualJson "\"1E3\""
//            "2E-1" lenientShouldEqualJson "0.2"
//            "2E-1" lenientShouldEqualJson "\"0.2\""
//            "0.2" lenientShouldEqualJson "\"2e-1\""
//            "5E0" lenientShouldEqualJson "5.0"
//         }
//
//         test("Strings with numbers") {
//            shouldFail {
//               "\"abc 123\"" lenientShouldEqualJson "123"
//            }.shouldHaveMessage(
//               """
//                  The top level expected number but was string
//
//                  expected:
//                  123
//
//                  actual:
//                  "abc 123"
//               """.trimIndent()
//            )
//
//            shouldFail {
//               "123" lenientShouldEqualJson "\"abc 123\""
//            }
//         }
//
//         test("booleans in strings are ok") {
//            "true" lenientShouldEqualJson "\"true\""
//            "\"true\"" lenientShouldEqualJson "true"
//         }
//
//         test("float and int can be mixed, if exactly same") {
//            "1.0" lenientShouldEqualJson "1"
//            "1" lenientShouldEqualJson "1.0"
//         }
//
//         test("Dont trim necessary zeroes") {
//            shouldFail {
//               "10" lenientShouldEqualJson "1.0"
//            }
//
//            shouldFail {
//               "1" lenientShouldEqualJson "10.0"
//            }
//         }
//
//         test("high-precision float with only trailing zeros") {
//            "1" lenientShouldEqualJson "1.0000000000000000000000000"
//            "1.0000000000000000000000000" lenientShouldEqualJson "1"
//         }
//      }
//   }
//)

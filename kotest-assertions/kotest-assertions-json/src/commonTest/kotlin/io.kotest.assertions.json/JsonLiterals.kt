import io.kotest.assertions.json.CompareMode
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.throwable.shouldHaveMessage

fun jsonLiteralTests() = funSpec {
   test("comparing float and int") {
      shouldFail {
         "3.2" shouldEqualJson "3"
      }.shouldHaveMessage(
         """
               The top level expected 3 but was 3.2

               expected:
               3

               actual:
               3.2
            """.trimIndent()
      )
   }

   test("comparing high-precision floating point numbers") {
      // TODO: Currently failing since expected has been parsed to a JsonTree and then encoded to string again
      //       Since it is a floating point number it is parsed to a double and back again when this happens,
      //       so expected also loses precision

      shouldFail {
         "0.12345678912345678" shouldEqualJson "0.123456789123456789"
      }.shouldHaveMessage(
         """
               The top level expected 0.123456789123456789 but was 0.12345678912345678

               expected:
               0.123456789123456789

               actual:
               0.12345678912345678
            """.trimIndent()
      )
   }

   test("comparing string and boolean") {
      shouldFail {
         "true" shouldEqualJson "\"true\""
      }.shouldHaveMessage(
         """
               The top level expected string but was boolean

               expected:
               "true"

               actual:
               true
            """.trimIndent()
      )
   }

   context("Lenient type-conversions") {

      infix fun String.lenientShouldEqualJson(expected: String) = this.shouldEqualJson(expected, CompareMode.Lenient)

      test("booleans in strings are ok") {
         "true" lenientShouldEqualJson "\"true\""
         "\"true\"" lenientShouldEqualJson "true"
      }

      test("float and int can be mixed, if exactly same") {
         "1.0" lenientShouldEqualJson "1"
         "1" lenientShouldEqualJson "1.0"
      }

      test("high-precision float with only trailing zeros") {
         "1" lenientShouldEqualJson "1.0000000000000000000000000"
         "1.0000000000000000000000000" lenientShouldEqualJson "1"
      }
   }
}

package io.kotest.assertions.json

import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.intellijFormatError
import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.contract

inline fun <reified T> String?.shouldContainJsonKeyValue(path: String, value: T) {
   contract {
      returns() implies (this@shouldContainJsonKeyValue != null)
   }

   this should containJsonKeyValue(path, value)
}

inline fun <reified T> String.shouldNotContainJsonKeyValue(path: String, value: T) =
   this shouldNot containJsonKeyValue(path, value)

inline fun <reified T> containJsonKeyValue(path: String, t: T) = object : Matcher<String?> {
   private fun keyIsAbsentFailure() = MatcherResult(
      false,
      { "Expected given to contain json key <'$path'> but key was not found." },
      { "Expected given to not contain json key <'$path'> but key was found." }
   )

   private fun invalidJsonFailure(actualJson: String?) = MatcherResult(
      false,
      { "Expected a valid JSON, but was ${if (actualJson == null) "null" else "empty" }" },
      { "Expected a valid JSON, but was ${if (actualJson == null) "null" else "empty" }" },
   )

   private fun extractKey(value: String?): T? {
      return try {
         JsonPath.parse(value).read(path, T::class.java)
      } catch (e: PathNotFoundException) {
         null
      } catch (e: InvalidPathException) {
         throw AssertionError("$path is not a valid JSON path")
      }
   }

   override fun test(value: String?): MatcherResult {
      if (value.isNullOrEmpty()) return invalidJsonFailure(value)

      val sub =
         if (value.length < 50) value.trim()
         else value.substring(0, 50).trim() + "..."

      val actualKeyValue = extractKey(value)
      val passed = t == actualKeyValue
      if (!passed && actualKeyValue == null) return keyIsAbsentFailure()

      return MatcherResult(
         passed,
         { "Value mismatch at '$path': ${intellijFormatError(Expected(t.print()), Actual(actualKeyValue.print()))}" },
         {
            "$sub should not contain the element $path = $t"
         }
      )
   }
}

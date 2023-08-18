package io.kotest.assertions.json

import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
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
   override fun test(value: String?): MatcherResult {
      val sub = when (value) {
         null -> value
         else -> if (value.length < 50) value.trim() else value.substring(0, 50).trim() + "..."
      }

      val passed = value != null && try {
         JsonPath.parse(value).read(path, T::class.java) == t
      } catch (e: PathNotFoundException) {
         false
      } catch (e: InvalidPathException) {
         throw AssertionError("$path is not a valid JSON path")
      }

      return MatcherResult(
         passed,
         { "$sub should contain the element $path = $t" },
         {
            "$sub should not contain the element $path = $t"
         }
      )
   }
}

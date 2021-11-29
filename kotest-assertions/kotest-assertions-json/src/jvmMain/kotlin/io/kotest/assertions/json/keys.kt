package io.kotest.assertions.json

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.contract

infix fun String?.shouldContainJsonKey(path: String) {
   contract {
      returns() implies (this@shouldContainJsonKey != null)
   }

   this should containJsonKey(path)
}

infix fun String.shouldNotContainJsonKey(path: String) = this shouldNot containJsonKey(path)
fun containJsonKey(path: String) = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val sub = when (value) {
         null -> value
         else -> if (value.length < 50) value.trim() else value.substring(0, 50).trim() + "..."
      }

      val passed = try {
         value != null && JsonPath.read<String>(value, path) != null
      } catch (t: PathNotFoundException) {
         false
      }

      return MatcherResult(
         passed,
         { "$sub should contain the path $path" },
         {
            "$sub should not contain the path $path"
         })
   }
}

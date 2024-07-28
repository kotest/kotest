package io.kotest.assertions.json

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

infix fun String.shouldNotContainJsonKey(path: String) =
   this shouldNot containJsonKey(path)

fun containJsonKey(path: String) = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val sub = when (value) {
         null -> return MatcherResult(
            false,
            { "null should contain the path $path" },
            { "null should not contain the path $path" },
         )
         else -> if (value.length < 50) value.trim() else value.substring(0, 50).trim() + "..."
      }

      return when(extractByPath(json = value, path = path, Any::class.java)) {
            is ExtractValueOutcome.ExtractedValue<*> -> {
               MatcherResult(
                  true,
                  { "$sub should contain the key $path" },
                  { "$sub should not contain the key $path" }
               )
            }
            is ExtractValueOutcome.JsonPathNotFound -> {
               val subPathDescription = findValidSubPath(value, path).description()
               MatcherResult(
                  false,
                  { "Expected given to contain json key <'$path'> but key was not found. $subPathDescription" },
                  { "Expected given to not contain json key <'$path'> but key was found." }
               )

            }
         }
   }
}
